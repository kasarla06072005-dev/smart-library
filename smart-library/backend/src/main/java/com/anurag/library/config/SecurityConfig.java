package com.anurag.library.config;

import com.anurag.library.filter.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Comma-separated list of allowed frontend origins, set via CORS_ALLOWED_ORIGINS
    // in production. Defaults to the local Vite dev server.
    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // These two handlers cover denials that happen in the security filter
            // chain itself (URL-pattern authorization below) - a plain
            // @ExceptionHandler in a @RestControllerAdvice only sees exceptions
            // thrown from inside a controller method, which a filter-level denial
            // never reaches. Without this, 401/403 here would fall back to Spring
            // Boot's generic error page instead of a clean JSON body the frontend
            // can read.
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Please log in to continue\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"You do not have permission to do that\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth
                // public
                .requestMatchers(HttpMethod.GET, "/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()

                // any authenticated user (student or admin)
                .requestMatchers(HttpMethod.GET, "/books", "/books/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/students/me").authenticated()
                .requestMatchers(HttpMethod.POST, "/borrow/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/borrow/my").authenticated()

                // admin-only - catalog management
                .requestMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")

                // admin-only - borrow oversight
                .requestMatchers(HttpMethod.GET, "/borrow/all").hasRole("ADMIN")

                // admin-only - user management
                .requestMatchers(HttpMethod.GET, "/students", "/students/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/students", "/students/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/students/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/students/**").hasRole("ADMIN")

                // admin-only - login anomaly dashboard
                .requestMatchers(HttpMethod.GET, "/login-attempts", "/login-attempts/**").hasRole("ADMIN")

                .anyRequest().denyAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
