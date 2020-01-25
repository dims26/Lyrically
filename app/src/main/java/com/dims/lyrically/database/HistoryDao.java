package com.dims.lyrically.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("Select * from history Order by title_featured")
    List<History> getHistory();
    @Insert
    void addHistory(History history);
    @Delete
    void deleteFromHistory(History history);
    @Query("DELETE FROM history")
    void clearHistory();
}