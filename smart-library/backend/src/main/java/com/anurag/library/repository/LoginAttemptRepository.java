package com.anurag.library.repository;

import com.anurag.library.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    List<LoginAttempt> findAllByOrderByAttemptedAtDesc();
    List<LoginAttempt> findByFlaggedTrueOrderByAttemptedAtDesc();
    List<LoginAttempt> findByEmailOrderByAttemptedAtDesc(String email);
    long countByEmailAndSuccessFalseAndAttemptedAtAfter(String email, LocalDateTime after);
    List<LoginAttempt> findByEmailAndSuccessTrue(String email);
}
