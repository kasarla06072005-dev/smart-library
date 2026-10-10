package com.anurag.library.seed;

import com.anurag.library.entity.Student;
import com.anurag.library.enums.Role;
import com.anurag.library.repository.StudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Creates exactly one admin account on first startup, ONLY if no admin exists yet.
// There is deliberately no public API that can create or promote to ADMIN - this
// seeder is the only path, and it only ever runs when the admin table is empty.
@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Value("${app.admin.email:admin@library.local}")
    private String adminEmail;

    @Value("${app.admin.password:ChangeMe123!}")
    private String adminPassword;

    private final StudentRepository repo;
    private final PasswordEncoder encoder;

    public AdminSeeder(StudentRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        Student admin = repo.findByEmail(adminEmail);
        if (admin == null) {
            admin = new Student();
            admin.setName("Smart Library Admin");
            admin.setEmail(adminEmail);
            admin.setRole(Role.ADMIN);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setLocked(false);
            repo.save(admin);
            log.info("Created admin account: {}", adminEmail);
        } else {
            admin.setRole(Role.ADMIN);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setLocked(false);
            repo.save(admin);
            log.info("Updated admin account credentials: {}", adminEmail);
        }
    }
}
