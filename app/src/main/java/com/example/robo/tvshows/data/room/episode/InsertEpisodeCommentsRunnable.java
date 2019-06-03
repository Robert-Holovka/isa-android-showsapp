package com.example.robo.tvshows.data.room.episode;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.Comment;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;

import java.util.List;

public class InsertEpisodeCommentsRunnable implements Runnable {

    private final DatabaseCallback<Void> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<Comment> comments;

    public InsertEpisodeCommentsRunnable(Context context, List<Comment> comments, DatabaseCallback<Void> callback) {
        this.context = context;
        this.comments = comments;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            RoomDatabaseFactory.db(context).episodeDao().insertComments(comments);
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
