package com.anurag.library.controller;

import com.anurag.library.dto.LoginAttemptView;
import com.anurag.library.service.LoginAttemptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/login-attempts")
public class LoginAttemptController {

    @Autowired
    private LoginAttemptService service;

    @GetMapping
    public List<LoginAttemptView> all() {
        return service.all();
    }

    @GetMapping("/flagged")
    public List<LoginAttemptView> flagged() {
        return service.flagged();
    }
}
