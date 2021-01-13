package com.dims.lyrically.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `search_cache` (`id` INTEGER, `full_title` TEXT, `title` TEXT, `thumbnail_url` TEXT, `url` TEXT, `title_featured` TEXT, `artist_name` TEXT, PRIMARY KEY(`id`))")
    }
}