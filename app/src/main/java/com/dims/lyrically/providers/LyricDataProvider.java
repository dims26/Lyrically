package com.dims.lyrically.providers;

import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LyricDataProvider{
    private static LyricDataProvider INSTANCE = null;
    private String queryResult;
    private LyricDataProvider(){}

    public static LyricDataProvider getInstance(){
        if (INSTANCE == null) {
            synchronized (LyricDataProvider.class) {
                INSTANCE = new LyricDataProvider();
            }
        }
        return INSTANCE;
    }

    /**
     *
     * @param query The query to be searched for
     * @return The API JSON response as a {@link String}. If call was not executed, an empty string
     * is returned.
     */
    public String search(String query){

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://genius.p.rapidapi.com/search?q=" + query)
                .get()
                .addHeader("x-rapidapi-host", "genius.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "***REMOVED***")
                .build();
        final String[] JSONResponse = new String[1];
        Response response = null;
        try {
            client.newCall(request)
                    //enqueue enables an asynchronous call and retrieval of the result via a callback
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            JSONResponse[0] = "dims said failed";
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            JSONResponse[0] = response.body().string();
                        }
                    });
            //From the rapidAPI test response, if there is a response, there will be a body. So there
            //is no need to worry about making string() method null safe
        }catch (Exception ex){
            ex.printStackTrace();
            return "";
        }
        return JSONResponse[0];
    }
}
