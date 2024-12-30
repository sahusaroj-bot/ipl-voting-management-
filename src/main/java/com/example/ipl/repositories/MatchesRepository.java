package com.example.ipl.repositories;

import com.example.ipl.model.Matches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface MatchesRepository extends JpaRepository<Matches, Long> {
    @Query("SELECT w FROM Matches w WHERE w.match_date = :match_date")
   List<Matches> findByMatch_date(LocalDate match_date);
}
