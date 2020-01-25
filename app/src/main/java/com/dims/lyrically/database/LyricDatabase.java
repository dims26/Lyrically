package com.dims.lyrically.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Favourites.class, History.class}, exportSchema = false, version = 1)
public abstract class LyricDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "lyric_database";
    private static LyricDatabase dbInstance;

    public static synchronized LyricDatabase getDbInstance(Context context){
        if (dbInstance == null){
            dbInstance = Room.databaseBuilder(context.getApplicationContext(), LyricDatabase.class,
                    DATABASE_NAME).fallbackToDestructiveMigration().build();
        }
        return dbInstance;
    }


    public abstract FavouritesDao favouritesDao();
    public abstract HistoryDao historyDao();
}
