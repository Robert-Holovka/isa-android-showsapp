package com.example.robo.tvshows.data.models;

import android.support.annotation.NonNull;
import com.squareup.moshi.Json;

public class MediaResponse {

    @Json(name = "_id")
    @NonNull
    private String id;

    @Json(name = "path")
    @NonNull
    private String path;

    @Json(name = "type")
    @NonNull
    private String type;

    public MediaResponse(@NonNull String id, @NonNull String path, @NonNull String type) {
        this.id = id;
        this.path = path;
        this.type = type;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    @NonNull
    public String getType() {
        return type;
    }
}
