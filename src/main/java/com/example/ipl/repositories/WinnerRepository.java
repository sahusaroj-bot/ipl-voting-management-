package com.example.ipl.repositories;

import com.example.ipl.model.Winner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
    public interface WinnerRepository extends JpaRepository<Winner,Long> {
    @Query("SELECT w FROM Winner w WHERE w.match_id = :match_id")
    Optional<Winner> findByMatch_id(Long match_id);
    }

