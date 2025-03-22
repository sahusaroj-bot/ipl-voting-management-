package com.example.ipl.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "vote")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long user_id;
    private Long match_id;
    private Long voted_team_id;
    private Long vote_time;
    private String username;
    private String voted_team_name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getMatch_id() {
        return match_id;
    }

    public void setMatch_id(Long match_id) {
        this.match_id = match_id;
    }

    public Long getVote_time() {
        return vote_time;
    }

    public void setVote_time(Long vote_time) {
        this.vote_time = vote_time;
    }

    public Long getVoted_team_id() {
        return voted_team_id;
    }

    public void setVoted_team_id(Long voted_team_id) {
        this.voted_team_id = voted_team_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVoted_team_name() {
        return voted_team_name;
    }

    public void setVoted_team_name(String voted_team_name) {
        this.voted_team_name = voted_team_name;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", match_id=" + match_id +
                ", voted_team_id=" + voted_team_id +
                ", vote_time=" + vote_time +
                ", username='" + username + '\'' +
                ", voted_team_name='" + voted_team_name + '\'' +
                '}';
    }
}

