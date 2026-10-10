package com.anurag.library.dto;

public class LoginResponseDTO {
    private String token;
    private Long id;
    private String name;
    private String email;
    private String role;

    public LoginResponseDTO(String token, Long id, String name, String email, String role) {
        this.token = token; this.id = id; this.name = name; this.email = email; this.role = role;
    }
    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
