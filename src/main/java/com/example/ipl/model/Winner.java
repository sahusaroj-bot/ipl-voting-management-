package com.example.ipl.model;

import jakarta.persistence.*;

@Entity
@Table(name = "winner")
public class Winner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long match_id;
    private String Team;
    private String winner_team;
    private  String winners;

    public Long getMatch_id() {
        return match_id;
    }

    public void setMatch_id(Long match_id) {
        this.match_id = match_id;
    }

    public String getTeam() {
        return Team;
    }

    public void setTeam(String team) {
        Team = team;
    }

    public String getWinner_team() {
        return winner_team;
    }

    public void setWinner_team(String winner_team) {
        this.winner_team = winner_team;
    }

    public String getWinners() {
        return winners;
    }

    public void setWinners(String winners) {
        this.winners = winners;
    }

    @Override
    public String toString() {
        return "Winner{" +
                "match_id=" + match_id +
                ", Team='" + Team + '\'' +
                ", winner_team='" + winner_team + '\'' +
                ", winners='" + winners + '\'' +
                '}';
    }
}