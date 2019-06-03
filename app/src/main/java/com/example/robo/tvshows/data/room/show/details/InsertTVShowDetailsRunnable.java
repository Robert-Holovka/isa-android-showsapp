package com.example.robo.tvshows.data.room.show.details;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.TVShowDetails;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;

public class InsertTVShowDetailsRunnable implements Runnable {

    private final DatabaseCallback<Void> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final TVShowDetails show;

    public InsertTVShowDetailsRunnable(Context context, TVShowDetails show, DatabaseCallback<Void> callback) {
        this.context = context;
        this.show = show;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            RoomDatabaseFactory.db(context).showDetailsDao().insertShowDetails(show);
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
