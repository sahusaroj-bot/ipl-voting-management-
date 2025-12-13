package com.example.ipl.model;

public class WinnerRequest {
    private String winnerTeam;
    private Long match_id;

    // Getters and setters
    public String getWinnerTeam() {
        return winnerTeam;
    }

    public void setWinnerTeamID(String winnerTeam) {
        this.winnerTeam = winnerTeam;
    }

    public Long getMatch_id() {
        return match_id;
    }

    public void setMatch_id(Long match_id) {
        this.match_id = match_id;
    }
}

