package com.dims.lyrically.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dims.lyrically.models.SearchCache

const val GET_LIMIT = 20

@Dao
interface SearchCacheDao {
    @Query("Select * from search_cache where title like '%' || :query || '%' or artist_name like '%' || :query || '%' Order by title_featured limit $GET_LIMIT")
    fun getSearchCache(query: String): List<SearchCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCache(vararg cache: SearchCache)

    @Query("DELETE FROM search_cache")
    fun clearCache()
}