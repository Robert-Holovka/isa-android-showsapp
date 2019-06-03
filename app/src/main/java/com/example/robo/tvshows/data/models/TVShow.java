package com.example.robo.tvshows.data.models;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

@Entity
public class TVShow {

    @Json(name = "_id")
    @PrimaryKey
    @NonNull
    private String ID;

    @Json(name = "title")
    @ColumnInfo(name = "title")
    private String title;


    @Json(name = "imageUrl")
    @ColumnInfo(name = "imageURL")
    private String imageURL;

    public TVShow(@NonNull String ID, String title, String imageURL) {
        this.ID = ID;
        this.title = title;
        this.imageURL = imageURL;
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getImageURL() {
        return imageURL;
    }
}
