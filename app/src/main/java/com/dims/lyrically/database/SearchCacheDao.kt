package com.dims.lyrically.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dims.lyrically.models.SearchCache

@Dao
interface SearchCacheDao {
    @Query("Select * from search_cache where full_title like '%' || :query || '%' Order by title_featured limit 20")
    fun getSearchCache(query: String): LiveData<List<SearchCache>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCache(vararg cache: SearchCache)

    @Query("DELETE FROM search_cache")
    fun clearCache()
}