package com.example.ipl.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "matches")
public class Matches {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String team1;
        private String team2;
        private Date match_date;
        private String winner;

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getTeam1() {
                return team1;
        }

        public void setTeam1(String team1) {
                this.team1 = team1;
        }

        public String getTeam2() {
                return team2;
        }

        public void setTeam2(String team2) {
                this.team2 = team2;
        }

        public Date getMatch_date() {
                return match_date;
        }

        public void setMatch_date(Date match_date) {
                this.match_date = match_date;
        }
        
        public String getWinner() {
                return winner;
        }
        
        public void setWinner(String winner) {
                this.winner = winner;
        }
    }
