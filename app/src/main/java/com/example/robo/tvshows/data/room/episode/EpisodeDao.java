package com.example.robo.tvshows.data.room.episode;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.robo.tvshows.data.models.Comment;
import com.example.robo.tvshows.data.models.Episode;
import com.example.robo.tvshows.data.models.EpisodeDetails;
import com.example.robo.tvshows.data.models.ShowWithEpisodes;
import com.example.robo.tvshows.data.models.TVShowDetails;

import java.util.List;

@Dao
public interface EpisodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllEpisodes(List<Episode> episodes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEpisodeDetails(EpisodeDetails episode);

    @Query("SELECT * FROM EpisodeDetails WHERE ID = :id")
    EpisodeDetails getEpisodeDetails(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComments(List<Comment> comments);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComment(Comment comments);

    @Query("SELECT * FROM Comment WHERE episodeId = :episodeId")
    List<Comment> getComments(String episodeId);
}
