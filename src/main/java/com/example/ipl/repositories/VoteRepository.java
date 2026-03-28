package com.example.ipl.repositories;

import com.example.ipl.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT v FROM Vote v WHERE v.user_id = :user_id AND v.match_id = :match_id")
    Optional<Vote> findByUser_idAndMatch_id(Long user_id, Long match_id);

    @Query("SELECT v FROM Vote v WHERE v.match_id = :match_id")
    List<Vote> findByMatch_id(Long match_id);
}
