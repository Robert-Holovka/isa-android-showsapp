package com.example.robo.tvshows.data.room.show;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.TVShow;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;

import java.util.List;


public class GetTVShowRunnable implements Runnable {

    private final DatabaseCallback<List<TVShow>> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public GetTVShowRunnable(Context context, DatabaseCallback<List<TVShow>> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            final List<TVShow> shows = RoomDatabaseFactory.db(context).showDao().getAllShows();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(shows);
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