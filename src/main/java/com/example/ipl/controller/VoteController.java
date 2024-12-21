package com.example.ipl.controller;

import com.example.ipl.model.Matches;
import com.example.ipl.model.Teams;
import com.example.ipl.model.User;
import com.example.ipl.model.Vote;
import com.example.ipl.repositories.MatchesRepository;
import com.example.ipl.repositories.TeamsRepository;
import com.example.ipl.repositories.UserRepository;
import com.example.ipl.repositories.VoteRepository;
import com.example.ipl.services.VoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

@RestController
public class VoteController {

    private static final Logger log = LoggerFactory.getLogger(VoteController.class);
    @Autowired
    VoteService voteService;

    @PostMapping("/Insertvote")
    public ResponseEntity<Object> insertVote(@RequestBody Vote userVote) {
        log.info(String.valueOf(userVote));
        try {
            voteService.addVote(userVote);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error adding vote", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}



