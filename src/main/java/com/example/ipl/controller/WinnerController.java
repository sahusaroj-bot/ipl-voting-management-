package com.example.ipl.controller;

import com.example.ipl.model.Matches;
import com.example.ipl.repositories.MatchesRepository;
import com.example.ipl.services.WinnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class WinnerController {

    private static final Logger log = LoggerFactory.getLogger(WinnerController.class);

    @Autowired
    MatchesRepository matchesRepository;

    @Autowired
    WinnerService winnerService;

    @GetMapping("/fetchWinner")
    public List<List<String>> fetchWinner(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Matches> matches = matchesRepository.findByMatch_date(date);
            if (matches.isEmpty()) {
                log.info("No matches found for date: {}", date);
                return new ArrayList<>();
            }
            List<Long> matchIds = matches.stream().map(Matches::getId).toList();
            return winnerService.getWinners(matchIds);
        } catch (Exception e) {
            log.error("Error fetching winners for date: {}", date, e);
            return new ArrayList<>();
        }
    }
}
