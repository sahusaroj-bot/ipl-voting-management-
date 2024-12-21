package com.example.ipl.services;

import com.example.ipl.controller.VoteController;
import com.example.ipl.model.Matches;
import com.example.ipl.model.Teams;
import com.example.ipl.model.User;
import com.example.ipl.model.Vote;
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

    public void addVote(Vote userVote){
        Vote vote= new Vote();
        Long user_id = userVote.getUser_id();
        Optional<User> user = userRepository.findById(userVote.getUser_id());
        Optional<Teams> teams= teamsRepository.findById(userVote.getVoted_team_id());
        Optional<Matches> matches= matchesRepository.findById(userVote.getMatch_id());
        if (user.isPresent() && (user_id.equals(user.get().getId()))) {
            vote.setUser_id(userVote.getUser_id());
            vote.setUsername(user.get().getUsername());
        }
        if (teams.isPresent() ) {
            String teamName = teams.get().getName();
            if (!teamName.isEmpty() && (teams.get().getId() == userVote.getVoted_team_id())) {
                vote.setVoted_team_id(userVote.getVoted_team_id());
                vote.setVoted_team_name(teamName);

            }
        }

        if(matches.isPresent() && matches.get().getId().equals(userVote.getMatch_id())) vote.setMatch_id(userVote.getMatch_id());
        vote.setVote_time(System.currentTimeMillis());
        voteRepository.save(vote);
    }
}
