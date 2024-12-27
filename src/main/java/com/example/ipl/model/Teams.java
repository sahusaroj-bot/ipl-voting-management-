package com.example.ipl.model;


import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Teams {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String team_name;

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Teams{" +
                "id=" + id +
                ", team_name='" + team_name + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
