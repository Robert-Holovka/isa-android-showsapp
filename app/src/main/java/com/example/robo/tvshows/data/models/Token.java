package com.example.robo.tvshows.data.models;

import com.squareup.moshi.Json;

public class Token {
    @Json(name="token")
    private String token;

    public Token(String token){
        this.token = token;
    }

    public String getToken(){
        return token;
    }
}
