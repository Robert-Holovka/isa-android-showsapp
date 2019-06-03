package com.example.robo.tvshows.data.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.robo.tvshows.data.models.Comment;
import com.example.robo.tvshows.data.models.Episode;
import com.example.robo.tvshows.data.models.EpisodeDetails;
import com.example.robo.tvshows.data.models.ShowLikeStatus;
import com.example.robo.tvshows.data.models.TVShow;
import com.example.robo.tvshows.data.models.TVShowDetails;
import com.example.robo.tvshows.data.room.episode.EpisodeDao;
import com.example.robo.tvshows.data.room.show.TVShowDao;
import com.example.robo.tvshows.data.room.show.details.TVShowDetailsDao;


@Database(
        entities = {
                TVShow.class,
                TVShowDetails.class,
                Episode.class,
                EpisodeDetails.class,
                Comment.class,
                ShowLikeStatus.class
        },
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TVShowDao showDao();
    public abstract TVShowDetailsDao showDetailsDao();
    public abstract EpisodeDao episodeDao();
}