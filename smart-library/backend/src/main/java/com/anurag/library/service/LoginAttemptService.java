package com.anurag.library.service;

import com.anurag.library.dto.LoginAttemptView;
import com.anurag.library.entity.LoginAttempt;
import com.anurag.library.entity.Student;
import com.anurag.library.repository.LoginAttemptRepository;
import com.anurag.library.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginAttemptService {

    @Autowired private LoginAttemptRepository repo;
    @Autowired private StudentRepository studentRepo;

    public List<LoginAttemptView> all() {
        return repo.findAllByOrderByAttemptedAtDesc().stream().map(this::toView).collect(Collectors.toList());
    }

    public List<LoginAttemptView> flagged() {
        return repo.findByFlaggedTrueOrderByAttemptedAtDesc().stream().map(this::toView).collect(Collectors.toList());
    }

    public String unlock(Long studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        student.setLocked(false);
        studentRepo.save(student);
        return "Account unlocked";
    }

    private LoginAttemptView toView(LoginAttempt a) {
        return new LoginAttemptView(a.getId(), a.getEmail(), a.getIpAddress(), a.isSuccess(),
                a.isFlagged(), a.getReason(), a.getAttemptedAt());
    }
}
