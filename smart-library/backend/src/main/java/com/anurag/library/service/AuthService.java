package com.anurag.library.service;

import com.anurag.library.dto.LoginDTO;
import com.anurag.library.dto.LoginResponseDTO;
import com.anurag.library.dto.RegisterDTO;
import com.anurag.library.entity.LoginAttempt;
import com.anurag.library.entity.Student;
import com.anurag.library.enums.Role;
import com.anurag.library.repository.LoginAttemptRepository;
import com.anurag.library.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {

    // failed attempts allowed before auto-lock, and the rolling window they're counted over
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int WINDOW_MINUTES = 2;

    @Autowired private StudentRepository repo;
    @Autowired private LoginAttemptRepository attemptRepo;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    // Always creates a STUDENT account. RegisterDTO has no role field by design -
    // admin accounts are provisioned only by AdminSeeder or by an existing admin.
    public ResponseEntity<?> register(RegisterDTO dto) {
        if (repo.existsByEmail(dto.getEmail())) {
            // generic message - do not reveal that this specific email is already registered
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not register with those details"));
        }
        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setRole(Role.STUDENT);
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        Student saved = repo.save(student);
        return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "name", saved.getName(),
                "email", saved.getEmail(),
                "role", saved.getRole().name()
        ));
    }

    public ResponseEntity<?> login(LoginDTO dto, String ipAddress, String userAgent) {
        Student existing = repo.findByEmail(dto.getEmail());

        // Same generic message whether the email doesn't exist or the password is
        // wrong - telling them apart lets an attacker enumerate valid accounts.
        final String badCreds = "Incorrect email or password";

        if (existing == null) {
            logAttempt(dto.getEmail(), ipAddress, userAgent, false, true, "Unknown email");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", badCreds));
        }

        if (existing.isLocked()) {
            logAttempt(dto.getEmail(), ipAddress, userAgent, false, true, "Account locked");
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(Map.of("error", "This account is locked due to suspicious activity. Contact an admin to unlock it."));
        }

        boolean passwordOk = passwordEncoder.matches(dto.getPassword(), existing.getPassword());

        if (!passwordOk) {
            logAttempt(dto.getEmail(), ipAddress, userAgent, false, false, null);
            LocalDateTime windowStart = LocalDateTime.now().minusMinutes(WINDOW_MINUTES);
            long recentFailures = attemptRepo.countByEmailAndSuccessFalseAndAttemptedAtAfter(dto.getEmail(), windowStart);
            if (recentFailures >= MAX_FAILED_ATTEMPTS) {
                existing.setLocked(true);
                repo.save(existing);
                LoginAttempt last = attemptRepo.findByEmailOrderByAttemptedAtDesc(dto.getEmail()).get(0);
                last.setFlagged(true);
                last.setReason(recentFailures + " failed attempts within " + WINDOW_MINUTES + " minutes - account auto-locked");
                attemptRepo.save(last);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", badCreds));
        }

        boolean knownIp = attemptRepo.findByEmailAndSuccessTrue(dto.getEmail()).stream()
                .anyMatch(a -> ipAddress != null && ipAddress.equals(a.getIpAddress()));
        boolean isFirstEverLogin = attemptRepo.findByEmailAndSuccessTrue(dto.getEmail()).isEmpty();
        String reason = (!knownIp && !isFirstEverLogin) ? "Login from a new IP address" : null;

        logAttempt(dto.getEmail(), ipAddress, userAgent, true, reason != null, reason);

        String token = jwtService.generateToken(existing.getEmail(), existing.getRole().name());
        return ResponseEntity.ok(new LoginResponseDTO(token, existing.getId(), existing.getName(),
                existing.getEmail(), existing.getRole().name()));
    }

    private void logAttempt(String email, String ip, String userAgent, boolean success, boolean flagged, String reason) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setEmail(email);
        attempt.setIpAddress(ip);
        attempt.setUserAgent(userAgent);
        attempt.setSuccess(success);
        attempt.setFlagged(flagged);
        attempt.setReason(reason);
        attemptRepo.save(attempt);
    }
}
