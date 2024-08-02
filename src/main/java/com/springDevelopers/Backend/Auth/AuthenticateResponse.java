package com.springDevelopers.Backend.Auth;

public class AuthenticateResponse {
    private Integer Id;
    private String firstname;
    private String email;
    private String role;
    private String token;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public AuthenticateResponse(String firstname, String email, String role, String token) {
        this.firstname = firstname;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public AuthenticateResponse(){

    }

    public AuthenticateResponse(Integer id, String email, String role, String token) {
        Id = id;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
