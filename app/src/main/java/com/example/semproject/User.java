package com.example.semproject;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;      // New field for phone number
    private String password;
    private String role;

    // Full constructor including phone
    public User(int id, String name, String email, String phone, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    // Partial constructor without phone and password (optional)
    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = "";
        this.password = "";
        this.role = "";
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}
