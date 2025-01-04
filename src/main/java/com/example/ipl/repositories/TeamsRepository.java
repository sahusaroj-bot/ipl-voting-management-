package com.example.ipl.repositories;

import com.example.ipl.model.Matches;
import com.example.ipl.model.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TeamsRepository extends JpaRepository<Teams,Long> {
    @Query("SELECT w FROM Teams w WHERE w.team_name = :team_name")
    Teams findByTeam_name(String team_name);
}
