package com.example.ipl.services;

import com.example.ipl.model.*;
import com.example.ipl.repositories.MatchesRepository;
import com.example.ipl.repositories.TeamsRepository;
import com.example.ipl.repositories.VoteRepository;
import com.example.ipl.repositories.WinnerRepository;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WinnerService {
    @Autowired
    MatchesRepository matchesRepository;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    TeamsRepository teamsRepository;
    @Autowired
    WinnerRepository winnerRepository;
    private  int count=0;


public void save_winner(WinnerRequest winnerRequest) {
    Optional<Teams> teams = teamsRepository.findById(winnerRequest.getWinnerTeamID());
    Optional<Matches> matches = matchesRepository.findById(winnerRequest.getMatch_id());
    if (matches.isPresent() && teams.isPresent()) {
        Matches matches2=matches.get();
     matches2.setWinner(teams.get().getTeam_name());
      matchesRepository.save(matches2);

    }
    }

    public void setWinnerFields(WinnerRequest winnerRequest) {
        Optional<Winner> existingWinnerOpt = winnerRepository.findByMatch_id(winnerRequest.getMatch_id());
        Winner winner;

        if (existingWinnerOpt.isPresent()) {
            winner = existingWinnerOpt.get();
        } else {
            winner = new Winner();
            winner.setMatch_id(winnerRequest.getMatch_id());
        }

        setTeam(winnerRequest.getWinnerTeamID(), winner);
        setWinners(winnerRequest, winner);
        winnerRepository.save(winner);
    }

    private void setTeam(Long teamID, Winner winner) {
        Optional<Matches> matches = matchesRepository.findById(winner.getMatch_id());
        Optional<Teams> teams = teamsRepository.findById(teamID);
        String teamMatch = "";

        if (matches.isPresent()) {
            teamMatch = matches.get().getTeam1() + " vs " + matches.get().getTeam2();
        }

        winner.setTeam(teamMatch);

        if (teams.isPresent()) {
            winner.setWinner_team(teams.get().getTeam_name());
        }
    }

    private void setWinners(WinnerRequest winnerRequest, Winner winner) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Vote> votes = voteRepository.findAll()
                .stream()
                .filter(value -> Objects.equals(value.getMatch_id(), winnerRequest.getMatch_id()))
                .toList();

        for (Vote vote : votes) {
            if (Objects.equals(vote.getVoted_team_id(), winnerRequest.getWinnerTeamID())) {
                stringBuilder.append(vote.getUsername()).append(", ");
            }
        }

        // Remove trailing comma and space, if necessary
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        winner.setWinners(stringBuilder.toString());
    }

}



