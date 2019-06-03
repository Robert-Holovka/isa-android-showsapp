package com.example.robo.tvshows.data.models;

import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

public class NewEpisode {


    @Json(name = "showId")
    @NonNull
    private String showId;

    @Json(name = "mediaId")
    @NonNull
    private String mediaId;

    @Json(name = "title")
    @NonNull
    private String title;

    @Json(name = "description")
    @NonNull
    private String description;

    @Json(name = "episodeNumber")
    @NonNull
    private String episodeNumber;

    @Json(name = "season")
    @NonNull
    private String season;

    public NewEpisode(@NonNull String showId, @NonNull String mediaId, @NonNull String title, @NonNull String description, @NonNull String episodeNumber, @NonNull String season) {
        this.showId = showId;
        this.mediaId = mediaId;
        this.title = title;
        this.description = description;
        this.episodeNumber = episodeNumber;
        this.season = season;
    }

    @NonNull
    public String getShowId() {
        return showId;
    }

    @NonNull
    public String getMediaId() {
        return mediaId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getEpisodeNumber() {
        return episodeNumber;
    }

    @NonNull
    public String getSeason() {
        return season;
    }
}
