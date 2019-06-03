package com.example.robo.tvshows.data.room.show;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.robo.tvshows.data.models.TVShow;

import java.util.List;

@Dao
public interface TVShowDao {

    @Query("SELECT * FROM TVShow")
    List<TVShow> getAllShows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllShows(List<TVShow> tvShows);
}
