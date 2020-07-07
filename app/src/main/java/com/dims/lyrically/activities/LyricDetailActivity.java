package com.dims.lyrically.activities;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dims.lyrically.AppExecutors;
import com.dims.lyrically.R;
import com.dims.lyrically.database.Favourites;
import com.dims.lyrically.database.History;
import com.dims.lyrically.database.LyricDatabase;
import com.dims.lyrically.models.Song;

import java.util.ArrayList;


public class LyricDetailActivity extends AppCompatActivity {
    WebView lyricWebView;
    Song song;
    private LyricDatabase db;
    private ProgressBar detailProgressBar;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_detail);

        song = (Song) getIntent().getSerializableExtra("song");

        String url = null;
        String title = null;
        if (song != null) {
            url = song.getUrl();
            title = song.getTitle();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        detailProgressBar = findViewById(R.id.detail_progressBar);
        detailProgressBar.setVisibility(View.GONE);
        detailProgressBar.setMax(100);
        detailProgressBar.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
        detailProgressBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        lyricWebView = findViewById(R.id.lyrics_webView);
        lyricWebView.setWebViewClient(new LyricWebViewClient());
        lyricWebView.setWebChromeClient(new LyricWebChromeClientDemo());
        lyricWebView.getSettings().setJavaScriptEnabled(true);
        lyricWebView.loadUrl(url);

        refreshLayout = findViewById(R.id.webView_refresher);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lyricWebView.reload();
                refreshLayout.setRefreshing(false);
            }
        });

        db = LyricDatabase.getDbInstance(getBaseContext());
        addToHistory();
    }

    private void addToHistory() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int historyFlag = 0;
                ArrayList<History> historyArrayList = (ArrayList<History>) db.historyDao().getHistory().getValue();
                final History history = new History(song.getId(), song.getFullTitle(), song.getTitle(),
                        song.getSongArtImageThumbnailUrl(), song.getUrl(), song.getTitleWithFeatured(),
                        song.getArtistName());
                for (History dbHistory : historyArrayList) {
                    if (dbHistory.getId() == history.getId()) {
                        historyFlag = 1;
                    }
                }
                if (historyFlag == 0) {
                    db.historyDao().addHistory(history);

                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("history", "Added " + history.getFullTitle() + " to history database");
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lyric_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.favourite_lyric){
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    Favourites fav = new Favourites(song.getId(), song.getFullTitle(), song.getTitle(),
                            song.getSongArtImageThumbnailUrl(), song.getUrl(), song.getTitleWithFeatured(),
                            song.getArtistName());
                    int favFlag = 0;
                    ArrayList<Favourites> favouritesArrayList = (ArrayList<Favourites>) db.favouritesDao().getFavourites().getValue();
                    for (Favourites dbFav : favouritesArrayList){
                        if (dbFav.getId() == fav.getId()){
                            favFlag = 1;
                        }
                    }
                    if (favFlag == 0) {
                        db.favouritesDao().addFavourite(fav);
                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Added to favourites", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        db.favouritesDao().deleteFromFavourite(fav);
                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Removed from favourites", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }
        if (item.getItemId() == android.R.id.home){//If the action bar back button is pressed
            finish();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (lyricWebView.canGoBack()) {
                    lyricWebView.goBack();
                } else {
                    finish();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private class LyricWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            detailProgressBar.setProgress(100);
            detailProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            detailProgressBar.setVisibility(View.VISIBLE);
            detailProgressBar.setProgress(0);
        }
    }

    private class LyricWebChromeClientDemo extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            detailProgressBar.setProgress(progress);
        }
    }
}
