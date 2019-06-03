package com.example.robo.tvshows.data.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

import java.util.List;


@Entity
public class TVShowDetails {

    @Json(name = "_id")
    @PrimaryKey
    @NonNull
    private String ID;

    @Json(name = "title")
    @ColumnInfo(name = "title")
    private String title;

    @Json(name = "imageUrl")
    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

    @Json(name = "likesCount")
    @ColumnInfo(name = "likesCount")
    private int likesCount;

    @Json(name = "description")
    @ColumnInfo(name = "description")
    private String description;

    public TVShowDetails(@NonNull  String ID, String title, String imageUrl, int likesCount, String description) {
        this.ID = ID;
        this.title = title;
        this.imageUrl = imageUrl;
        this.likesCount = likesCount;
        this.description = description;
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getLikesCount(){
        return likesCount;
    }

    public String getDescription() {
        return description;
    }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

}
