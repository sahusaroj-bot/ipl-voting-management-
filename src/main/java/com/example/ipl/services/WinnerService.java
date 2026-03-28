package com.example.ipl.services;

import com.example.ipl.model.*;
import com.example.ipl.repositories.*;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WinnerService {

    private static final Logger log = LoggerFactory.getLogger(WinnerService.class);

    @Autowired
    MatchesRepository matchesRepository;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    WinnerRepository winnerRepository;
    @Autowired
    UserRepository userRepository;

    /**
     * Single transactional method:
     * 1. Sets winner on the match
     * 2. Creates/updates winner record
     * 3. Distributes money to winning voters
     * All steps succeed or all roll back.
     */
    @Transactional
    public void processWinner(WinnerRequest winnerRequest, Matches match) {
        String winnerTeam = winnerRequest.getWinnerTeam();
        Long matchId = winnerRequest.getMatch_id();

        // Step 1: Set winner on match
        match.setWinner(winnerTeam);
        matchesRepository.save(match);
        log.info("Winner set on match {}: {}", matchId, winnerTeam);

        // Step 2: Build winner record
        Optional<Winner> existingOpt = winnerRepository.findByMatch_id(matchId);
        Winner winner = existingOpt.orElse(new Winner());
        winner.setMatch_id(matchId);
        winner.setTeam(match.getTeam1() + " vs " + match.getTeam2());
        winner.setWinner_team(winnerTeam);

        // Step 3: Find all votes for this match that voted for the winning team
        List<Vote> allMatchVotes = voteRepository.findByMatch_id(matchId);

        List<Long> winnerUserIDs = new ArrayList<>();
        StringBuilder winnerNames = new StringBuilder();

        for (Vote vote : allMatchVotes) {
            if (winnerTeam.equals(vote.getVoted_team_name())) {
                winnerUserIDs.add(vote.getUser_id());
                winnerNames.append(vote.getUsername()).append(",");
            }
        }

        // Remove trailing comma
        if (!winnerNames.isEmpty()) {
            winnerNames.setLength(winnerNames.length() - 1);
        }

        winner.setWinners(winnerNames.toString());
        winnerRepository.save(winner);
        log.info("Winner record saved for match {}. Winners: {}", matchId, winnerNames);

        // Step 4: Distribute money
        if (winnerUserIDs.isEmpty()) {
            log.info("No users voted for the winning team in match {}", matchId);
            return;
        }

        long totalUsers = userRepository.count();
        double pool = totalUsers * 10.0;
        double moneyPerWinner = pool / winnerUserIDs.size();
        LocalDateTime istNow = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        log.info("Prize pool: {} | Winners: {} | Per winner: {}", pool, winnerUserIDs.size(), moneyPerWinner);

        for (Long userID : winnerUserIDs) {
            Optional<User> userOpt = userRepository.findById(userID);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setTotalAmount(user.getTotalAmount() + moneyPerWinner);
                user.setLastSavedAmount(moneyPerWinner);
                user.setLastUpdatedDate(istNow);
                userRepository.save(user);
                log.info("Added {} to user {}", moneyPerWinner, user.getUsername());
            }
        }
    }

    public List<List<String>> getWinners(List<Long> matchIDs) {
        List<List<String>> result = new ArrayList<>();
        for (Long matchID : matchIDs) {
            List<String> matchWinners = new ArrayList<>();
            Optional<Winner> winnerOpt = winnerRepository.findByMatch_id(matchID);
            if (winnerOpt.isPresent() && winnerOpt.get().getWinners() != null) {
                for (String s : winnerOpt.get().getWinners().split(",")) {
                    if (!s.isBlank()) matchWinners.add(s.trim());
                }
            }
            result.add(matchWinners);
        }
        return result;
    }

    public List<MatchResultDTO> getMatchResults(List<Matches> matches) {
        long totalUsers = userRepository.count();
        double pool = totalUsers * 10.0;

        List<MatchResultDTO> results = new ArrayList<>();
        for (Matches match : matches) {
            Optional<Winner> winnerOpt = winnerRepository.findByMatch_id(match.getId());

            if (winnerOpt.isEmpty() || winnerOpt.get().getWinner_team() == null) {
                results.add(new MatchResultDTO(
                        match.getId(), match.getTeam1(), match.getTeam2(),
                        null, new ArrayList<>(), 0, pool));
                continue;
            }

            Winner winner = winnerOpt.get();
            List<String> winnerNames = winner.getWinners() == null ? new ArrayList<>()
                    : Arrays.stream(winner.getWinners().split(","))
                            .map(String::trim).filter(s -> !s.isBlank())
                            .collect(Collectors.toList());

            double amountPerWinner = winnerNames.isEmpty() ? 0 : pool / winnerNames.size();

            results.add(new MatchResultDTO(
                    match.getId(), match.getTeam1(), match.getTeam2(),
                    winner.getWinner_team(), winnerNames, amountPerWinner, pool));
        }
        return results;
    }
}
