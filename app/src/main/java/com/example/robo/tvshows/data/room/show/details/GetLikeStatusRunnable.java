package com.example.robo.tvshows.data.room.show.details;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.ShowLikeStatus;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;

public class GetLikeStatusRunnable implements Runnable {

    private final DatabaseCallback<ShowLikeStatus> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String ID;

    public GetLikeStatusRunnable(Context context, String ID, DatabaseCallback<ShowLikeStatus> callback) {
        this.context = context;
        this.callback = callback;
        this.ID = ID;
    }

    @Override
    public void run() {
        try {
            final ShowLikeStatus likeStatus = RoomDatabaseFactory.db(context).showDetailsDao().getLikeStatus(ID);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(likeStatus);
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
