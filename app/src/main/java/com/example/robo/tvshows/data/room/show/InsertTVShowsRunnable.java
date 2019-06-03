package com.example.robo.tvshows.data.room.show;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.TVShow;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;

import java.util.List;


public class InsertTVShowsRunnable implements Runnable {

    private final DatabaseCallback<Void> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<TVShow> shows;

    public InsertTVShowsRunnable(Context context, List<TVShow> shows, DatabaseCallback<Void> callback) {
        this.context = context;
        this.shows = shows;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            RoomDatabaseFactory.db(context).showDao().insertAllShows(shows);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(null);
                }
            });
        } catch (final Exception e) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(e);
                }
            });
        }
    }
}