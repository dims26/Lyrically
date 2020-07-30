package com.dims.lyrically.utils;

import android.content.Context;

import com.dims.lyrically.R;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public  class LyricDataProvider{
    private Context context;
    private OkHttpClient client;

    public LyricDataProvider(Context context, OkHttpClient client){
        this.context = context;
        this.client = client;
    }

    /**
     * @param query The query to be searched for
     * @param callback The callback which defines the actions to be taken depending on the success
     *                 of the network call
     */
    public void search(String query, Callback callback) {
        Request request = new Request.Builder()
                .url("https://genius.p.rapidapi.com/search?q=" + query)
                .get()
                .addHeader("x-rapidapi-host", "genius.p.rapidapi.com")
                .addHeader("x-rapidapi-key", context.getResources().getString(R.string.api_key))
                .build();


        //enqueue enables an asynchronous call and retrieval of the result via a callback
        client.newCall(request)
                .enqueue(callback);
    }
}
