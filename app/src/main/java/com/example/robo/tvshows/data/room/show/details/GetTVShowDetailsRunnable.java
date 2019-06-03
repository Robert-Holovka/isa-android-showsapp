package com.example.robo.tvshows.data.room.show.details;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.ShowWithEpisodes;
import com.example.robo.tvshows.data.room.RoomDatabaseFactory;


public class GetTVShowDetailsRunnable implements  Runnable{

    private final DatabaseCallback<ShowWithEpisodes> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String showID;

    public GetTVShowDetailsRunnable(Context context, String showID, DatabaseCallback<ShowWithEpisodes> callback) {
        this.context = context;
        this.callback = callback;
        this.showID = showID;
    }

    @Override
    public void run() {
        try {
            final ShowWithEpisodes showDetails = RoomDatabaseFactory.db(context).showDetailsDao().getShowDetails(showID);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(showDetails);
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
