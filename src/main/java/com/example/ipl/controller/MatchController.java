package com.example.ipl.controller;

import com.example.ipl.model.Matches;
import com.example.ipl.model.TodayVoteDTO;
import com.example.ipl.model.Vote;
import com.example.ipl.repositories.MatchesRepository;
import com.example.ipl.repositories.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MatchController {

    @Autowired
    MatchesRepository matchesRepository;

    @Autowired
    VoteRepository voteRepository;

    private static final Logger log = LoggerFactory.getLogger(MatchController.class);

    @GetMapping(value = "/by-date")
    public List<Matches> responseEntity(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            return matchesRepository.findByMatch_date(date);
        } catch (Exception e) {
            log.error("failed to fetch the matches by this date");
            return null;
        }
    }

    @GetMapping("/today-votes")
    public ResponseEntity<List<TodayVoteDTO>> getTodayVotes() {
        LocalDateTime nowIST = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        LocalDate today = nowIST.toLocalDate();

        List<Matches> matches = matchesRepository.findByMatch_date(today);
        if (matches.isEmpty()) return ResponseEntity.ok(new ArrayList<>());

        List<TodayVoteDTO> result = new ArrayList<>();
        for (Matches match : matches) {
            boolean deadlinePassed = match.getVotingDeadline() != null && nowIST.isAfter(match.getVotingDeadline());

            List<String> team1Voters = new ArrayList<>();
            List<String> team2Voters = new ArrayList<>();

            if (deadlinePassed) {
                List<Vote> votes = voteRepository.findByMatch_id(match.getId());
                team1Voters = votes.stream()
                        .filter(v -> match.getTeam1().equals(v.getVoted_team_name()))
                        .map(Vote::getUsername).collect(Collectors.toList());
                team2Voters = votes.stream()
                        .filter(v -> match.getTeam2().equals(v.getVoted_team_name()))
                        .map(Vote::getUsername).collect(Collectors.toList());
            }

            result.add(new TodayVoteDTO(
                    match.getId(), match.getTeam1(), match.getTeam2(),
                    team1Voters, team2Voters,
                    match.getVotingDeadline(), deadlinePassed));
        }
        return ResponseEntity.ok(result);
    }
}
