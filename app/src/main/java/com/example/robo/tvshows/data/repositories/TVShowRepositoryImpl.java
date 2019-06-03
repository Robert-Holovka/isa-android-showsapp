package com.example.robo.tvshows.data.repositories;

import android.content.Context;

import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.TVShow;
import com.example.robo.tvshows.data.room.show.GetTVShowRunnable;
import com.example.robo.tvshows.data.room.show.InsertTVShowsRunnable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TVShowRepositoryImpl implements TVShowRepository {

    private final Context context;
    private final ExecutorService executor;
    private Future task;

    public TVShowRepositoryImpl(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
    }


    @Override
    public void getTVShows(DatabaseCallback<List<TVShow>> callback) {
        cancel();
        this.task = executor.submit(new GetTVShowRunnable(context, callback));
    }

    @Override
    public void insertTVShows(List<TVShow> shows, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executor.submit(new InsertTVShowsRunnable(context, shows, callback));
    }

    private void cancel() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }
}

interface TVShowRepository {
    void getTVShows(DatabaseCallback<List<TVShow>> callback);
    void insertTVShows(List<TVShow> shows, DatabaseCallback<Void> callback);
}