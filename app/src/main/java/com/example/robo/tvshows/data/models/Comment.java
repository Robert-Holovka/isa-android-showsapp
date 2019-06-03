package com.example.robo.tvshows.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import com.squareup.moshi.Json;

@Entity
public class Comment {

    @Json(name = "_id")
    @PrimaryKey
    @NonNull
    private String ID;

    @Json(name = "text")
    @ColumnInfo(name = "text")
    private String text;

    @Json(name = "episodeId")
    @ColumnInfo(name = "episodeId")
    private String episodeId;

    @Json(name = "userEmail")
    @ColumnInfo(name = "userEmail")
    private String userEmail;

    public Comment(@NonNull String ID, String text, String episodeId, String userEmail) {
        this.ID = ID;
        this.text = text;
        this.episodeId = episodeId;
        this.userEmail = userEmail;
    }

    @NonNull
    public String getID() {
        return ID;
    }

    public String getText() {
        return text;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
