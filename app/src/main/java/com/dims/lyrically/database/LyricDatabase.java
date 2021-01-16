package com.dims.lyrically.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dims.lyrically.models.Favourites;
import com.dims.lyrically.models.History;
import com.dims.lyrically.models.SearchCache;

import java.io.Serializable;

@Database(entities = {Favourites.class, History.class, SearchCache.class}, exportSchema = false, version = 2)
public abstract class LyricDatabase extends RoomDatabase implements Serializable {
    private static final String DATABASE_NAME = "lyric_database";
    private static LyricDatabase dbInstance;

    public static synchronized LyricDatabase getDbInstance(Context context){
        if (dbInstance == null){
            dbInstance = Room.databaseBuilder(context.getApplicationContext(),
                    LyricDatabase.class,
                    DATABASE_NAME)
                    .addMigrations(MigrationsKt.getMIGRATION_1_2())
                    .build();
        }
        return dbInstance;
    }


    public abstract FavouritesDao favouritesDao();
    public abstract HistoryDao historyDao();
    public abstract SearchCacheDao searchCacheDao();
}
