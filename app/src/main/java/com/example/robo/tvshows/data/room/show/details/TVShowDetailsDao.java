package com.example.robo.tvshows.data.room.show.details;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.robo.tvshows.data.models.ShowLikeStatus;
import com.example.robo.tvshows.data.models.ShowWithEpisodes;
import com.example.robo.tvshows.data.models.TVShowDetails;

@Dao
public interface TVShowDetailsDao {
    @Query("SELECT * FROM TVShowDetails WHERE ID = :id")
    ShowWithEpisodes getShowDetails(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertShowDetails(TVShowDetails tvShow);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLikeStatus(ShowLikeStatus status);

    @Query("SELECT * FROM ShowLikeStatus WHERE ID = :ID")
    ShowLikeStatus getLikeStatus(String ID);
}
