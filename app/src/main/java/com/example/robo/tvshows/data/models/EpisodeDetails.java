package com.example.robo.tvshows.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

@Entity
public class EpisodeDetails {

    @Json(name = "_id")
    @PrimaryKey
    @NonNull
    private String ID;

    @Json(name = "title")
    @ColumnInfo(name = "title")
    private String title;

    @Json(name = "description")
    @ColumnInfo(name = "description")
    private String description;

    @Json(name = "imageUrl")
    @ColumnInfo(name = "imageUrl")
    private String imageURL;

    @Json(name = "showId")
    @ColumnInfo(name = "showId")
    private String showId;

    @Json(name = "episodeNumber")
    @ColumnInfo(name = "episodeNumber")
    private String episodeNumber;

    @Json(name = "season")
    @ColumnInfo(name = "season")
    private String season;

    @Json(name = "type")
    @ColumnInfo(name = "type")
    private String type;

    public EpisodeDetails(@NonNull String ID, String title, String description,
                          String imageURL, String showId, String episodeNumber, String season, String type) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.showId = showId;
        this.episodeNumber = episodeNumber;
        this.season = season;
        this.type = type;
    }

    @NonNull
    public String getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getShowId() {
        return showId;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public String getSeason() {
        return season;
    }

    public String getType() {
        return type;
    }
}
