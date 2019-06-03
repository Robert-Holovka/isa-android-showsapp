package com.example.robo.tvshows.data.room.episode;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.EpisodeDetails;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;

public class GetEpisodeDetailsRunnable implements Runnable {

    private final DatabaseCallback<EpisodeDetails> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String episodeId;

    public GetEpisodeDetailsRunnable(Context context, String episodeId, DatabaseCallback<EpisodeDetails> callback) {
        this.context = context;
        this.callback = callback;
        this.episodeId = episodeId;
    }

    @Override
    public void run() {
        try {
            final EpisodeDetails episode = RoomDatabaseFactory.db(context).episodeDao().getEpisodeDetails(episodeId);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(episode);
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
