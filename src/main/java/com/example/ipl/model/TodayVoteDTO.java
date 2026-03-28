package com.example.ipl.model;

import java.time.LocalDateTime;
import java.util.List;

public class TodayVoteDTO {
    private Long matchId;
    private String team1;
    private String team2;
    private List<String> team1Voters;
    private List<String> team2Voters;
    private LocalDateTime votingDeadline;
    private boolean deadlinePassed;

    public TodayVoteDTO(Long matchId, String team1, String team2,
                        List<String> team1Voters, List<String> team2Voters,
                        LocalDateTime votingDeadline, boolean deadlinePassed) {
        this.matchId = matchId;
        this.team1 = team1;
        this.team2 = team2;
        this.team1Voters = team1Voters;
        this.team2Voters = team2Voters;
        this.votingDeadline = votingDeadline;
        this.deadlinePassed = deadlinePassed;
    }

    public Long getMatchId() { return matchId; }
    public String getTeam1() { return team1; }
    public String getTeam2() { return team2; }
    public List<String> getTeam1Voters() { return team1Voters; }
    public List<String> getTeam2Voters() { return team2Voters; }
    public LocalDateTime getVotingDeadline() { return votingDeadline; }
    public boolean isDeadlinePassed() { return deadlinePassed; }
}
