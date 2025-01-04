package com.example.ipl.model;

public class VoteRequest {
    private Long user_id;
    private Long match_id;
    private String voted_team_name;

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

    public String getVoted_team_name() {
        return voted_team_name;
    }

    public void setVoted_team_name(String voted_team_name) {
        this.voted_team_name = voted_team_name;
    }

    @Override
    public String toString() {
        return "VoteRequest{" +
                "user_id=" + user_id +
                ", match_id=" + match_id +
                ", voted_team_name='" + voted_team_name + '\'' +
                '}';
    }
}
