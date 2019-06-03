package com.example.robo.tvshows.data.models;

import com.squareup.moshi.Json;

public class User {

    @Json(name = "email")
    private String email;

    @Json(name = "password")
    private String password;

    public User( String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
