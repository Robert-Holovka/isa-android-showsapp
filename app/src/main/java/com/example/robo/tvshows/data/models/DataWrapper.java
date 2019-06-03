package com.example.robo.tvshows.data.models;

import com.squareup.moshi.Json;


public class DataWrapper<T> {
    @Json(name = "data")
    private T data;

    public T getData() {
        return data;
    }
}
