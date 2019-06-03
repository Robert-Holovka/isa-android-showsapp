package com.example.robo.tvshows.data.models;

import android.support.annotation.NonNull;
import com.squareup.moshi.Json;

public class NewComment {

    @Json(name = "text")
    @NonNull
    private String text;

    @Json(name = "episodeId")
    @NonNull
    private String episodeId;

    public NewComment(@NonNull String text, @NonNull String episodeId) {
        this.text = text;
        this.episodeId = episodeId;
    }

    @NonNull
    public String getText() {
        return text;
    }

    @NonNull
    public String getEpisodeId() {
        return episodeId;
    }
}
