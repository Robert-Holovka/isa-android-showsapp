package com.example.robo.tvshows.data.repositories;

import android.content.Context;

import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.Comment;
import com.example.robo.tvshows.data.models.Episode;
import com.example.robo.tvshows.data.models.EpisodeDetails;
import com.example.robo.tvshows.data.room.episode.GetEpisodeCommentsRunnable;
import com.example.robo.tvshows.data.room.episode.GetEpisodeDetailsRunnable;
import com.example.robo.tvshows.data.room.episode.InsertEpisodeCommentRunnable;
import com.example.robo.tvshows.data.room.episode.InsertEpisodeCommentsRunnable;
import com.example.robo.tvshows.data.room.episode.InsertEpisodeDetailsRunnable;
import com.example.robo.tvshows.data.room.episode.InsertEpisodesRunnable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EpisodeRepositoryImpl implements EpisodeRepository {

    private final Context context;
    private final ExecutorService executor;
    private Future task;

    public EpisodeRepositoryImpl(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void insertEpisodes(List<Episode> episodes, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executor.submit(new InsertEpisodesRunnable(context,episodes, callback));
    }

    @Override
    public void insertDetails(EpisodeDetails episode, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executor.submit(new InsertEpisodeDetailsRunnable(context, episode, callback));
    }

    @Override
    public void getDetails(String episodeId, DatabaseCallback<EpisodeDetails> callback) {
        cancel();
        this.task = executor.submit(new GetEpisodeDetailsRunnable(context, episodeId, callback));
    }

    @Override
    public void getComments(String episodeId, DatabaseCallback<List<Comment>> callback) {
        cancel();
        this.task = executor.submit(new GetEpisodeCommentsRunnable(context, episodeId, callback));
    }

    @Override
    public void insertComments(List<Comment> comments, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executor.submit(new InsertEpisodeCommentsRunnable(context, comments, callback));

    }

    @Override
    public void insertComment(Comment comment, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executor.submit(new InsertEpisodeCommentRunnable(context, comment, callback));
    }

    private void cancel() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }
}

interface EpisodeRepository {
    void insertEpisodes(List<Episode> episodes, DatabaseCallback<Void> callback);
    void insertDetails(EpisodeDetails episode, DatabaseCallback<Void> callback);
    void getDetails(String episodeId, DatabaseCallback<EpisodeDetails> callback);
    void getComments(String episodeId, DatabaseCallback<List<Comment>> callback);
    void insertComments(List<Comment> comments, DatabaseCallback<Void> callback);
    void insertComment(Comment comment, DatabaseCallback<Void> callback);
}
