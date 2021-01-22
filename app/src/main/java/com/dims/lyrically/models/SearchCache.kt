package com.dims.lyrically.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "search_cache")
data class SearchCache(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "full_title") val fullTitle: String,
        val title: String,
        @ColumnInfo(name = "thumbnail_url") val songArtImageThumbnailUrl: String,
        val url: String,
        @ColumnInfo(name = "title_featured") val titleWithFeatured: String,
        @ColumnInfo(name = "artist_name") val artistName: String,
        @ColumnInfo(name = "expires") val expiryDate: Date = Date(Date().time + 2_592_000_000)
)