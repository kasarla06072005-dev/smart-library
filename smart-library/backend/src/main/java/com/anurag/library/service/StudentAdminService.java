package com.anurag.library.service;

import com.anurag.library.dto.StudentRequestDTO;
import com.anurag.library.dto.StudentView;
import com.anurag.library.entity.BorrowRecord;
import com.anurag.library.entity.Student;
import com.anurag.library.enums.Role;
import com.anurag.library.repository.BorrowRecordRepository;
import com.anurag.library.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentAdminService {

    @Autowired
    private StudentRepository repo;

    @Autowired
    private BorrowRecordRepository borrowRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityUtil securityUtil;

    public List<StudentView> all() {
        return repo.findAll().stream().map(this::toView).collect(Collectors.toList());
    }

    public StudentView getById(Long id) {
        Student s = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return toView(s);
    }

    public StudentView create(StudentRequestDTO dto) {
        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        Student s = new Student();
        s.setName(dto.getName());
        s.setEmail(dto.getEmail());
        s.setPassword(passwordEncoder.encode(dto.getPassword()));
        Role role = "ADMIN".equalsIgnoreCase(dto.getRole()) ? Role.ADMIN : Role.STUDENT;
        s.setRole(role);
        s.setLocked(false);
        Student saved = repo.save(s);
        return toView(saved);
    }

    public StudentView update(Long id, StudentRequestDTO dto) {
        Student s = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (!s.getEmail().equalsIgnoreCase(dto.getEmail()) && repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        s.setName(dto.getName());
        s.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (dto.getPassword().length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters");
            }
            s.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            Role newRole = "ADMIN".equalsIgnoreCase(dto.getRole()) ? Role.ADMIN : Role.STUDENT;
            s.setRole(newRole);
        }

        Student updated = repo.save(s);
        return toView(updated);
    }

    @Transactional
    public void delete(Long id) {
        Student s = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Student current = securityUtil.currentStudent();
        if (current != null && current.getId().equals(id)) {
            throw new IllegalArgumentException("You cannot delete your own account while logged in");
        }

        List<BorrowRecord> borrows = borrowRepo.findByStudentIdOrderByBorrowedAtDesc(id);
        boolean hasUnreturned = borrows.stream().anyMatch(b -> !b.isReturned());
        if (hasUnreturned) {
            throw new IllegalArgumentException("Cannot delete user: student currently has unreturned borrowed books");
        }

        if (!borrows.isEmpty()) {
            borrowRepo.deleteAll(borrows);
        }

        repo.delete(s);
    }

    private StudentView toView(Student s) {
        return new StudentView(s.getId(), s.getName(), s.getEmail(), s.getRole().name(), s.isLocked(), s.getCreatedAt());
    }
}
