package com.dims.lyrically.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dims.lyrically.models.History;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("Select * from history Order by title_featured")
    LiveData<List<History>> getHistory();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addHistory(History history);
    @Update
    int updateHistory(History... history);
    @Delete
    void deleteFromHistory(History history);
    @Query("DELETE FROM history")
    void clearHistory();
    @Query("SELECT COUNT(*) from history where id = :id")
    int usersCount(int id);
}