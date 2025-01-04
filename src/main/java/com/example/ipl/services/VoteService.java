package com.example.ipl.services;

import com.example.ipl.controller.VoteController;
import com.example.ipl.model.*;
import com.example.ipl.repositories.MatchesRepository;
import com.example.ipl.repositories.TeamsRepository;
import com.example.ipl.repositories.UserRepository;
import com.example.ipl.repositories.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {

    private static final Logger log = LoggerFactory.getLogger(VoteController.class);
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TeamsRepository teamsRepository;
    @Autowired
    MatchesRepository matchesRepository;

    public void addVote(VoteRequest userVote){
        Vote vote = new Vote();

        // Fetch user by using the voter's user ID
          Optional<User> user = userRepository.findById(userVote.getUser_id());
        if (user.isPresent()) {
            vote.setUser_id(userVote.getUser_id());
            vote.setUsername(user.get().getUsername());
        } else {
            log.error("User not found with ID: " + userVote.getUser_id());
            return;
        }

        // Fetch team by team name
        Teams teams = teamsRepository.findByTeam_name(userVote.getVoted_team_name());
        if (teams == null) {
            log.error("Team not found with name: " + userVote.getVoted_team_name());
            return;
        } else {
            log.info("Team found: " + teams.getName() + " (ID: " + teams.getId() + ")");
        }

        // Fetch match by match ID
        Optional<Matches> matches = matchesRepository.findById(userVote.getMatch_id());
        if (!matches.isPresent()) {
            log.error("Match not found with ID: " + userVote.getMatch_id());
            return;
        } else {
            log.info("Match found: " + matches.get().getId() + " involving " + matches.get().getTeam1() + " and " + matches.get().getTeam2());
        }

        // Set team details if they match
        String teamName = teams.getTeam_name();
        if (!teamName.isEmpty() && (matches.get().getTeam1().equals(teamName) || matches.get().getTeam2().equals(teamName))) {
            vote.setVoted_team_id(teams.getId());
            vote.setVoted_team_name(teamName);
        } else {
            log.error("Team name does not match any teams in the match: " + teamName);
            return;
        }

        // Set match ID and vote time
        vote.setMatch_id(userVote.getMatch_id());
        vote.setVote_time(System.currentTimeMillis());

        // Save vote
        voteRepository.save(vote);
        log.info("Vote saved successfully for user: " + userVote.getUser_id() + ", team: " + teamName + ", match: " + userVote.getMatch_id());
    }
}
