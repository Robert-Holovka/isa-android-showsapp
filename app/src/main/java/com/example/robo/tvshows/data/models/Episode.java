package com.example.robo.tvshows.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

@Entity
public class Episode implements Parcelable {

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

    @ColumnInfo(name = "showID")
    private transient String showID;

    @Json(name = "episodeNumber")
    @ColumnInfo(name = "episodeNumber")
    private String episodeNumber;

    @Json(name = "season")
    @ColumnInfo(name = "season")
    private String season;

    public Episode(String ID, String title, String description, String imageURL, String episodeNumber, String season) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.episodeNumber = episodeNumber;
        this.season = season;
    }

    protected Episode(Parcel in) {
        ID = in.readString();
        title = in.readString();
        description = in.readString();
        imageURL = in.readString();
        episodeNumber = in.readString();
        season = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageURL);
        dest.writeString(episodeNumber);
        dest.writeString(season);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

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

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public String getSeason() {
        return season;
    }

    public String getShowID() {return showID;}

    public void setShowID(String showID) {
        this.showID = showID;
    }
}
