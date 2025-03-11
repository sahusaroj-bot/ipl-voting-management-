package com.example.ipl.controller;

import com.example.ipl.model.Matches;
import com.example.ipl.model.Vote;
import com.example.ipl.model.WinnerRequest;
import com.example.ipl.repositories.MatchesRepository;
import com.example.ipl.repositories.VoteRepository;
import com.example.ipl.services.WinnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public ResponseEntity<Object> insertVote(@RequestBody WinnerRequest winnerRequest) {
        try {
            winnerService.save_winnerTeam(winnerRequest);
            winnerService.setWinnerFields(winnerRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error adding vote", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(value = "/fetchWinner")
    public List<List<String>> responseEntity(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<List<String>> winners = new ArrayList<>();
            List<Long> matchIds = new ArrayList<>();
            List<Matches> matches = matchesRepository.findByMatch_date(date);
            if (matches.isEmpty()) {
                log.error("No matches found for this date");
                return null;
            } else {
                for (Matches match : matches) {
                    matchIds.add(match.getId());
                }
            }
            return winnerService.getWinners(matchIds);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

