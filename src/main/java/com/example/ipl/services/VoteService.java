package com.example.ipl.services;

import com.example.ipl.controller.VoteController;
import com.example.ipl.model.*;
import com.example.ipl.repositories.MatchesRepository;
import com.example.ipl.repositories.UserRepository;
import com.example.ipl.repositories.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class VoteService {

    private static final Logger log = LoggerFactory.getLogger(VoteController.class);
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MatchesRepository matchesRepository;

    public void addOrUpdateVote(VoteRequest userVote) {
        // Check match exists and voting deadline
        Optional<Matches> matchOpt = matchesRepository.findById(userVote.getMatch_id());
        if (matchOpt.isEmpty()) {
            log.error("Match not found with ID: " + userVote.getMatch_id());
            throw new RuntimeException("Match not found");
        }

        Matches match = matchOpt.get();
        if (match.getVotingDeadline() != null) {
            LocalDateTime nowIST = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            if (nowIST.isAfter(match.getVotingDeadline())) {
                throw new RuntimeException("Voting is closed for this match");
            }
        }

        // Validate voted team is part of this match
        String votedTeam = userVote.getVoted_team_name();
        if (!votedTeam.equals(match.getTeam1()) && !votedTeam.equals(match.getTeam2())) {
            log.error("Team name does not match any teams in the match: " + votedTeam);
            throw new RuntimeException("Invalid team for this match");
        }

        // One vote per user per match — create or update
        Optional<Vote> existingVote = voteRepository.findByUser_idAndMatch_id(userVote.getUser_id(), userVote.getMatch_id());

        Vote vote;
        if (existingVote.isPresent()) {
            vote = existingVote.get();
            log.info("Updating vote for user: " + userVote.getUser_id() + ", match: " + userVote.getMatch_id());
        } else {
            vote = new Vote();
            vote.setUser_id(userVote.getUser_id());
            vote.setMatch_id(userVote.getMatch_id());
            log.info("Creating vote for user: " + userVote.getUser_id() + ", match: " + userVote.getMatch_id());
        }

        // Fetch user
        Optional<User> user = userRepository.findById(userVote.getUser_id());
        if (user.isEmpty()) {
            log.error("User not found with ID: " + userVote.getUser_id());
            throw new RuntimeException("User not found");
        }
        vote.setUsername(user.get().getUsername());

        // Set team name directly from match — no teams table dependency
        vote.setVoted_team_name(votedTeam);
        vote.setVote_time(System.currentTimeMillis());

        voteRepository.save(vote);
        log.info("Vote successfully saved for user: " + userVote.getUser_id() + ", team: " + votedTeam + ", match: " + userVote.getMatch_id());
    }

}
