package com.anurag.library.controller;

import com.anurag.library.dto.StudentRequestDTO;
import com.anurag.library.dto.StudentView;
import com.anurag.library.entity.Student;
import com.anurag.library.service.SecurityUtil;
import com.anurag.library.service.StudentAdminService;
import com.anurag.library.service.LoginAttemptService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired private SecurityUtil securityUtil;
    @Autowired private StudentAdminService adminService;
    @Autowired private LoginAttemptService loginAttemptService;

    // Any authenticated user can see their own profile - identity comes from the
    // token (SecurityUtil), never from a client-supplied id.
    @GetMapping("/me")
    public Map<String, Object> me() {
        Student s = securityUtil.currentStudent();
        return Map.of("id", s.getId(), "name", s.getName(), "email", s.getEmail(), "role", s.getRole().name());
    }

    @GetMapping
    public List<StudentView> all() {
        return adminService.all();
    }

    @GetMapping("/{id}")
    public StudentView getById(@PathVariable Long id) {
        return adminService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentView create(@Valid @RequestBody StudentRequestDTO dto) {
        return adminService.create(dto);
    }

    @PutMapping("/{id}")
    public StudentView update(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO dto) {
        return adminService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        adminService.delete(id);
        return Map.of("message", "Student deleted successfully");
    }

    @PostMapping("/{id}/unlock")
    public String unlock(@PathVariable Long id) {
        return loginAttemptService.unlock(id);
    }
}
