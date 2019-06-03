package com.example.robo.tvshows.data.room.episode;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.EpisodeDetails;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;


public class InsertEpisodeDetailsRunnable implements Runnable {

    private final DatabaseCallback<Void> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final EpisodeDetails episode;

    public InsertEpisodeDetailsRunnable(Context context, EpisodeDetails episode, DatabaseCallback<Void> callback) {
        this.context = context;
        this.episode = episode;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            RoomDatabaseFactory.db(context).episodeDao().insertEpisodeDetails(episode);
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
