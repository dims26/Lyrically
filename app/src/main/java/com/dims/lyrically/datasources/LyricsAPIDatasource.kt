package com.dims.lyrically.datasources

import android.content.Context
import com.dims.lyrically.R
import com.dims.lyrically.models.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class LyricsAPIDatasource @Inject constructor(@ApplicationContext private val context: Context,
                                              private val client: OkHttpClient,
                                              @Named("DispatcherIO") private val dispatcher: CoroutineDispatcher,
                                              @Named("GeniusGson") private val gson: Gson
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
            .addHeader("X-RapidAPI-Key", context.resources.getString(R.string.api_key))
            .build()

        return withContext(dispatcher) {
            val response = client.newCall(request).execute()

            if (response.code != 200) throw IOException()

            gson.fromJson(response.body?.string(), object : TypeToken<Song>() {}.type)
        }
    }
}