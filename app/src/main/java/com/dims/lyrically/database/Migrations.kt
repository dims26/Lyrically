package com.dims.lyrically.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `search_cache` (`id` INTEGER NOT NULL, `full_title` TEXT NOT NULL, `title` TEXT NOT NULL, `thumbnail_url` TEXT NOT NULL, `url` TEXT NOT NULL, `title_featured` TEXT NOT NULL, `artist_name` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }
}