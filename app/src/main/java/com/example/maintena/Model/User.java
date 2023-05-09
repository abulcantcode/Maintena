package com.example.maintena.Model;

// This is the object that the user details are stored in
public class User {
    String name, username, email;
    Boolean dealer;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getDealer() {
        return dealer;
    }

    public void setDealer(Boolean dealer) {
        this.dealer = dealer;
    }
}