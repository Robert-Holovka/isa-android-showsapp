package com.example.robo.tvshows.data.room.show.details;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.ShowLikeStatus;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;

public class InsertLikeStatusRunnable implements Runnable {

    private final DatabaseCallback<Void> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ShowLikeStatus status;

    public InsertLikeStatusRunnable(Context context, ShowLikeStatus status, DatabaseCallback<Void> callback) {
        this.context = context;
        this.callback = callback;
        this.status = status;
    }

    @Override
    public void run() {
        try {
            RoomDatabaseFactory.db(context).showDetailsDao().insertLikeStatus(status);
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
