package com.example.ipl.model;

public class WinnerRequest {
    private Long winnerTeamID;
    private Long match_id;

    // Getters and setters
    public Long getWinnerTeamID() {
        return winnerTeamID;
    }

    public void setWinnerTeamID(Long winnerTeamID) {
        this.winnerTeamID = winnerTeamID;
    }

    public Long getMatch_id() {
        return match_id;
    }

    public void setMatch_id(Long match_id) {
        this.match_id = match_id;
    }
}

