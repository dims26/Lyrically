package com.dims.lyrically.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavouritesDao {
    @Query("Select * from favourites Order by title_featured")
    List<Favourites> getFavourites();
    @Insert
    void addFavourite(Favourites favourite);
    @Update
    void updateFavourite(Favourites favourite);
    @Delete
    void deleteFromFavourite(Favourites favourite);
}
