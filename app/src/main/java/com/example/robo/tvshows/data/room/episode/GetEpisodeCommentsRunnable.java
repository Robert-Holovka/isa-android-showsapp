package com.example.robo.tvshows.data.room.episode;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.Comment;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;

import java.util.List;

public class GetEpisodeCommentsRunnable implements Runnable {

    private final DatabaseCallback<List<Comment>> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String episodeId;

    public GetEpisodeCommentsRunnable(Context context, String episodeId, DatabaseCallback<List<Comment>> callback) {
        this.context = context;
        this.callback = callback;
        this.episodeId = episodeId;
    }

    @Override
    public void run() {
        try {
            final List<Comment> comments = RoomDatabaseFactory.db(context).episodeDao().getComments(episodeId);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(comments);
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
