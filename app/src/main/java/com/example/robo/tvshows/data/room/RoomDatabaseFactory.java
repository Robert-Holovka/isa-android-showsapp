package com.example.robo.tvshows.data.room;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.robo.tvshows.data.room.AppDatabase;


public class RoomDatabaseFactory {

    private static AppDatabase database = null;
    private static final String DB_NAME = "shows_database";

    private RoomDatabaseFactory() {
    }

    public static AppDatabase db(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
        }
        return database;
    }
}