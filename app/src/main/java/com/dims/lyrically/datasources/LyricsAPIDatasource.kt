package com.dims.lyrically.datasources

import android.content.Context
import com.dims.lyrically.R
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class LyricsAPIDatasource @Inject constructor(private val context: Context,
                                              private val client: OkHttpClient) {

    /**
     * @param query The query to be searched for
     * @param callback The callback which defines the actions to be taken depending on the success
     * of the network call
     */
    fun search(query: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url("https://genius-song-lyrics1.p.rapidapi.com/search/?q=$query&per_page=20")
            .get()
            .addHeader("X-RapidAPI-Host", "genius-song-lyrics1.p.rapidapi.com")
            .addHeader("X-RapidAPI-Key", context.resources.getString(R.string.api_key))
            .build()


        //enqueue enables an asynchronous call and retrieval of the result via a callback
        client.newCall(request)
            .enqueue(callback)
    }
}