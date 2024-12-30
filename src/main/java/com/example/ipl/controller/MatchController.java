package com.example.ipl.controller;

import com.example.ipl.model.Matches;
import com.example.ipl.model.User;
import com.example.ipl.repositories.MatchesRepository;
import com.example.ipl.repositories.UserRepository;
import com.example.ipl.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
public class MatchController {

    @Autowired
    MatchesRepository matchesRepository;

    private static final Logger log = LoggerFactory.getLogger(com.example.ipl.controller.UserController.class);

    @GetMapping(value = "/getMatcheByDate")
    public List<Matches> responseEntity(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            return matchesRepository.findByDate(date);

        } catch (Exception e) {
            log.error("failed to fetch the matches by this date");
            return null;
        }

    }
}
