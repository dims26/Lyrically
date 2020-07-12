package com.dims.lyrically.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dims.lyrically.models.Favourites;

import java.util.List;

@Dao
public interface FavouritesDao {
    @Query("Select * from favourites Order by title_featured")
    LiveData<List<Favourites>> getFavourites();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFavourite(Favourites favourite);
    @Update
    void updateFavourite(Favourites favourite);
    @Delete
    void deleteFromFavourite(Favourites favourite);
    @Query("SELECT COUNT(*) from favourites where id = :id")
    int usersCount(int id);
}
