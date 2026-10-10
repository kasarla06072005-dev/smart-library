package com.anurag.library.filter;

import com.anurag.library.entity.Student;
import com.anurag.library.repository.StudentRepository;
import com.anurag.library.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private StudentRepository repo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {
                String email = jwtService.extractUsername(token);

                // Always re-fetch the account instead of trusting the JWT's role claim
                // alone. This means a role change or an account lock takes effect on the
                // very next request, even if the old token has not expired yet.
                Student student = repo.findByEmail(email);

                if (student != null && !student.isLocked()) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            student, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + student.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                // if student is null or locked, we simply don't authenticate - the request
                // falls through as anonymous and protected endpoints will reject it with 401/403
            }
        }
        filterChain.doFilter(request, response);
    }
}
