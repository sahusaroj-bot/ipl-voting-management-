package com.example.ipl.model;

import java.util.List;

public class MatchResultDTO {
    private Long matchId;
    private String team1;
    private String team2;
    private String winnerTeam;
    private List<String> winners;
    private double amountPerWinner;
    private double totalPool;

    public MatchResultDTO(Long matchId, String team1, String team2, String winnerTeam,
                          List<String> winners, double amountPerWinner, double totalPool) {
        this.matchId = matchId;
        this.team1 = team1;
        this.team2 = team2;
        this.winnerTeam = winnerTeam;
        this.winners = winners;
        this.amountPerWinner = amountPerWinner;
        this.totalPool = totalPool;
    }

    public Long getMatchId() { return matchId; }
    public String getTeam1() { return team1; }
    public String getTeam2() { return team2; }
    public String getWinnerTeam() { return winnerTeam; }
    public List<String> getWinners() { return winners; }
    public double getAmountPerWinner() { return amountPerWinner; }
    public double getTotalPool() { return totalPool; }
}
