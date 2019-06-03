package com.example.robo.tvshows.data.room.episode;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.Comment;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;

import java.util.List;

public class InsertEpisodeCommentRunnable implements Runnable {

    private final DatabaseCallback<Void> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Comment comment;

    public InsertEpisodeCommentRunnable(Context context, Comment comment, DatabaseCallback<Void> callback) {
        this.context = context;
        this.comment = comment;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            RoomDatabaseFactory.db(context).episodeDao().insertComment(comment);
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
