package com.example.robo.tvshows.data.repositories;

import android.content.Context;

import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.ShowLikeStatus;
import com.example.robo.tvshows.data.models.ShowWithEpisodes;
import com.example.robo.tvshows.data.models.TVShowDetails;
import com.example.robo.tvshows.data.room.show.details.GetLikeStatusRunnable;
import com.example.robo.tvshows.data.room.show.details.GetTVShowDetailsRunnable;
import com.example.robo.tvshows.data.room.show.details.InsertLikeStatusRunnable;
import com.example.robo.tvshows.data.room.show.details.InsertTVShowDetailsRunnable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ShowDetailsRepositoryImpl implements  ShowDetailsRepository {

    private final Context context;
    private final ExecutorService executor;
    private Future task;

    public ShowDetailsRepositoryImpl(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
    }
    @Override
    public void getShowDetails(String showID, DatabaseCallback<ShowWithEpisodes> callback) {
        cancel();
        this.task = executor.submit(new GetTVShowDetailsRunnable(context, showID, callback));
    }

    @Override
    public void insertShowDetails(TVShowDetails show, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executor.submit(new InsertTVShowDetailsRunnable(context, show, callback));
    }

    @Override
    public void insertLikeStatus(ShowLikeStatus status, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executor.submit(new InsertLikeStatusRunnable(context, status, callback));
    }

    @Override
    public void getLikeStatus(String showId, DatabaseCallback<ShowLikeStatus> callback) {
        cancel();
        this.task = executor.submit(new GetLikeStatusRunnable(context, showId, callback));
    }

    private void cancel() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }
}

interface ShowDetailsRepository {
    void getShowDetails(String showID, DatabaseCallback<ShowWithEpisodes> callback);
    void insertShowDetails(TVShowDetails show, DatabaseCallback<Void> callback);
    void insertLikeStatus(ShowLikeStatus status, DatabaseCallback<Void> callback);
    void getLikeStatus(String showId, DatabaseCallback<ShowLikeStatus> callback);
}
