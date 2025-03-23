package com.example.ipl.services;

import com.example.ipl.model.*;
import com.example.ipl.repositories.*;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import org.hibernate.dialect.function.ListaggStringAggEmulation;
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
    @Autowired
    UserRepository userRepository;  //To add the money to the user who voted for the winning team

/* Save the winner Team for the particular match */
    public void save_winnerTeam(WinnerRequest winnerRequest) {
        // we are fetching the team name to put it in the winner field of the match
        Optional<Teams> teams = teamsRepository.findById(winnerRequest.getWinnerTeamID());
        //fetching matches id for which we have to set the winner
        Optional<Matches> match = matchesRepository.findById(winnerRequest.getMatch_id());
        if (match.isPresent() && teams.isPresent()) {
            Matches matches = match.get();
            matches.setWinner(teams.get().getTeam_name());
            matchesRepository.save(matches);

        }

    }
/*WHEN WE SAVE THE WINNER TEAM WE ALSO HAVE TO SAVE THE USERS WHO VOTED FOR THAT TEAM AS WE CAN FETCH THE WINNER TEAM FROM THE WINNER TABLE AND FROM THE VOTE WE WE CAN FETCH THE VOTERS WHO VOTED FOR THE  WINNER TEAM WITH MATCH id AND TEAM ID
  WHY TEAM ID = IN VOTE TABLE VOTE IS STORED AS TEAM ID SO WE FETCH THE TEAM*/

    public void setWinnerFields(WinnerRequest winnerRequest) {
        Optional<Winner> existingWinnerOpt = winnerRepository.findById(winnerRequest.getMatch_id());
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
        int count =0;
        List<Long> userIDs= new ArrayList<>();
        List<Vote> votes = voteRepository.findAll()
                .stream()
                .filter(value -> Objects.equals(value.getMatch_id(), winnerRequest.getMatch_id()))
                .toList();
        for (Vote vote : votes) {
            if (Objects.equals(vote.getVoted_team_id(), winnerRequest.getWinnerTeamID())) {
                stringBuilder.append(vote.getUsername()).append(",");
                userIDs.add(vote.getUser_id());
                count++;
            }
        }
        setMoney(userIDs,count);

        // Remove trailing comma and space, if necessary
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        winner.setWinners(stringBuilder.toString());
    }
    private void setMoney(List<Long> userIDs,int count) {
        List<User> user = userRepository.findAll();
        int total =user.size()*10;
        double money = (double) total /count;
       for(Long userID:userIDs){
           Optional<User> winnerID =userRepository.findById(userID);
           if(winnerID.isPresent()){
                User user1 = winnerID.get();
                user1.setTotalAmount((user1.getTotalAmount()+money));
                userRepository.save(user1);

           }
        }
    }

    public List<List<String>> getWinners(List<Long> matchIDs) {
        List<List<String>> winners = new ArrayList<>();
        int count = 0;
        for (Long matchID : matchIDs) {
            winners.add(new ArrayList<>());
            String winner = winnerRepository.findByMatch_id(matchID);
            if (null != winner) {
                String[] winnerArray = winner.split(",");
                for (String s : winnerArray) {
                    winners.get(count).add(s);
                }
                count++;
            }
        }
        return winners;
    }
}




