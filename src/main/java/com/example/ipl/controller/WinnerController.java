package com.example.ipl.controller;

import com.example.ipl.model.Vote;
import com.example.ipl.model.WinnerRequest;
import com.example.ipl.repositories.MatchesRepository;
import com.example.ipl.repositories.VoteRepository;
import com.example.ipl.services.WinnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WinnerController {
    private static final Logger log = LoggerFactory.getLogger(WinnerController.class);
    @Autowired
    MatchesRepository matchesRepository;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    WinnerService winnerService;


    @PostMapping("/setWinner")
    public ResponseEntity<Object> insertVote(@RequestBody WinnerRequest winnerRequest){
      try{
          winnerService.save_winner(winnerRequest);
          winnerService.setWinnerFields(winnerRequest);
          return new ResponseEntity<>(HttpStatus.OK);
      }
      catch (Exception e){
          log.error("Error adding vote", e);
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }


}
