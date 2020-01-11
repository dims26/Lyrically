package com.dims.lyrically.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dims.lyrically.R;
import com.dims.lyrically.adapters.SearchListItemRecyclerAdapter;
import com.dims.lyrically.models.Song;
import com.dims.lyrically.providers.LyricDataProvider;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchListItemRecyclerAdapter recyclerAdapter = new SearchListItemRecyclerAdapter();
    Callback songCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setSupportActionBar((Toolbar) findViewById(R.id.search_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.search_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();//sets the focus on searchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO call provider method for getting query result and update recycler adapter
                if (!query.equals("")){
                    recyclerAdapter.mSongs.clear();
                    new LyricDataProvider().search(query,
                            SearchActivity.this, getSongCallback());

                }else {
                    recyclerAdapter.mSongs.clear();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerAdapter.notifyDataSetChanged();
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public Callback getSongCallback() {
        songCallback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(SearchActivity.this, "operation failed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //call SearchJSONParser and return ArrayList of model class
                if (response.code() == 200) {
                   ArrayList<Song> songs = new LyricDataProvider().extractJSONFeatures(response.body().string(), SearchActivity.this);
                   recyclerAdapter.mSongs.clear();
                   recyclerAdapter.mSongs.addAll(songs);
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           recyclerAdapter.notifyDataSetChanged();
                       }
                   });
                }
            }
        };
        return songCallback;
    }
}
