package com.anurag.library.service;

import com.anurag.library.entity.Student;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Resolves "who is making this request" from the authenticated JWT principal only.
// Endpoints that return "my" data (my borrow history, my profile) call this instead
// of trusting an id the client puts in the URL or request body - that trust is
// exactly how an IDOR vulnerability (one user reading another user's data just by
// changing a number in the URL) happens.
@Component
public class SecurityUtil {
    public Student currentStudent() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Student student) {
            return student;
        }
        throw new IllegalStateException("No authenticated user in context");
    }
}
