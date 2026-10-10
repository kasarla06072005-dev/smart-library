package com.anurag.library.repository;

import com.anurag.library.entity.Student;
import com.anurag.library.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);
    boolean existsByEmail(String email);
    long countByRole(Role role);
}
