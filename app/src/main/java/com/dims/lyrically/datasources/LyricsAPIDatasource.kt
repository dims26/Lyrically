package com.dims.lyrically.datasources

import com.dims.lyrically.models.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class LyricsAPIDatasource @Inject constructor(private val client: OkHttpClient,
                                              @Named("DispatcherIO") private val dispatcher: CoroutineDispatcher,
                                              @Named("GeniusGson") private val gson: Gson,
                                              @Named("TokenString") private val tokenString: String
) {

    /**
     * @param query The query to be searched for
     * @return A list of deserialised Song objects
     */
    suspend fun search(query: String) : List<Song> {
        val request: Request = Request.Builder()
            .url("https://genius-song-lyrics1.p.rapidapi.com/search/?q=$query&per_page=20")
            .get()
            .addHeader("X-RapidAPI-Host", "genius-song-lyrics1.p.rapidapi.com")
            .addHeader("X-RapidAPI-Key", tokenString)
            .build()

        return withContext(dispatcher) {
            val response = client.newCall(request).execute()

            if (response.code != 200) throw IOException()

            gson.fromJson(response.body?.string(), object : TypeToken<Song>() {}.type)
        }
    }
}