package com.dims.lyrically.providers;

import android.content.Context;
import android.widget.Toast;

import com.dims.lyrically.activities.SearchActivity;
import com.dims.lyrically.models.Song;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public  class LyricDataProvider{
    private String queryResult;
    public LyricDataProvider(){}

    /**
     *
     * @param query The query to be searched for
     * @param context Context of the calling application, used to send toast messages to the user
     * @param callback
     * @return The API JSON response as a {@link String}. If call was not executed, an empty string
     * is returned.
     */
    public void search(String query, final Context context, Callback callback) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://genius.p.rapidapi.com/search?q=" + query)
                .get()
                .addHeader("x-rapidapi-host", "genius.p.rapidapi.com")
                .addHeader("x-rapidapi-key", API_KEY)
                .build();
        final String[] JSONResponse = new String[1];
        final Response[] httpResponse = new Response[1];

        client.newCall(request)
                //enqueue enables an asynchronous call and retrieval of the result via a callback
                .enqueue(callback);
    }

    public ArrayList<Song> extractJSONFeatures(String responseBody, Context context) {
        ArrayList<Song> songs = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(responseBody);
            JSONObject response = baseJsonResponse.getJSONObject("response");

            JSONArray hits = response.getJSONArray("hits");
            for (int i = 0; i < hits.length(); i++){
                JSONObject currentHit = hits.getJSONObject(i);

                String type = currentHit.getString("type");

                JSONObject result = currentHit.getJSONObject("result");
                String fullTitle = result.getString("full_title");
                String title = result.getString("title");
                String songArtImageThumbnailUrl = result.getString("song_art_image_thumbnail_url");
                String url = result.getString("url");
                String titleWithFeatured = result.getString("title_with_featured");
                int id = result.getInt("id");

                JSONObject primary_artist = result.getJSONObject("primary_artist");
                String artistName = primary_artist.getString("name");

                Song song = new Song(fullTitle, title, songArtImageThumbnailUrl, url,
                        titleWithFeatured, id, artistName);
                if(type.equals("song"))
                    songs.add(song);
            }
            //Return List of songs
            return songs;
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error parsing JSON", Toast.LENGTH_LONG).show();
            return new ArrayList<Song>();
        }
    }
}
