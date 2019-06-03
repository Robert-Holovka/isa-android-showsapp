package com.example.robo.tvshows.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class ShowLikeStatus {

    @PrimaryKey
    @NonNull
    private String ID;

    @ColumnInfo(name = "likeStatus")
    private String likeStatus;

    public ShowLikeStatus(@NonNull String ID, String likeStatus) {
        this.ID = ID;
        this.likeStatus = likeStatus;
    }

    @NonNull
    public String getID() {
        return ID;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public boolean isLiked(){

        if(likeStatus == null){
            return false;
        }

        if(likeStatus.equals("liked")){
            return true;
        }
        return false;
    }

    public boolean isDisliked(){

        if(likeStatus == null){
            return false;
        }

        if(likeStatus.equals("disliked")){
            return true;
        }
        return false;
    }

    public void like(){
        likeStatus = "liked";
    }

    public void dislike(){
        likeStatus = "disliked";
    }
}
