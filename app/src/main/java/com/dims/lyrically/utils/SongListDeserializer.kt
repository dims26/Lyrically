package com.dims.lyrically.utils

import com.dims.lyrically.models.Song
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import javax.inject.Inject

class SongListDeserializer @Inject constructor() : JsonDeserializer<List<Song>?> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<Song> {
        val jsonObject = json!!.asJsonObject
        val hits = jsonObject["hits"].asJsonArray

        val songs = mutableListOf<Song>()
        hits.forEach{
            val currentHit = it.asJsonObject
            if (currentHit["type"].asString != "song") return@forEach

            val result = currentHit["result"].asJsonObject
            val primaryArtist = result["primary_artist"].asJsonObject
            songs.add(
                Song(
                    result["full_title"].asString, result["title"].asString,
                    result["song_art_image_thumbnail_url"].asString, result["url"].asString,
                    result["title_with_featured"].asString, result["id"].asInt,
                    primaryArtist["name"].asString
                )
            )
        }
        return songs
    }
}