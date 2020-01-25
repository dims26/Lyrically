package com.dims.lyrically.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dims.lyrically.AppExecutors;
import com.dims.lyrically.R;
import com.dims.lyrically.adapters.SearchListItemRecyclerAdapter;
import com.dims.lyrically.listeners.RecyclerViewClickListener;
import com.dims.lyrically.listeners.RecyclerViewTouchListener;
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
    private ProgressBar searchProgressBar;
    Callback songCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setSupportActionBar((Toolbar) findViewById(R.id.search_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        searchProgressBar = findViewById(R.id.search_progressBar);
        searchProgressBar.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.search_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(SearchActivity.this, LyricDetailActivity.class);
                intent.putExtra("song", recyclerAdapter.mSongs.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        LinearLayout searchEditFrame = searchView.findViewById(R.id.search_edit_frame); // Get the Linear Layout
        // Get the associated LayoutParams and set leftMargin
        ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).leftMargin = 0;

        //Remove search icon to the left of the search edit text
        ImageView searchViewIcon = searchView.findViewById(R.id.search_mag_icon);
        ViewGroup linearLayoutSearchView =(ViewGroup) searchViewIcon.getParent();
        linearLayoutSearchView.removeView(searchViewIcon);

        searchView.setQueryHint("Song or Artist name");
        searchView.requestFocus();//sets the focus on searchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")){
                    recyclerAdapter.mSongs.clear();
                    new LyricDataProvider().search(query,
                            SearchActivity.this, getSongCallback());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchProgressBar.setVisibility(View.VISIBLE);
                        }
                    });
                }else {
                    recyclerAdapter.mSongs.clear();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchProgressBar.setVisibility(View.GONE);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){//If the action bar back button is pressed
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public Callback getSongCallback() {
        songCallback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                AppExecutors.getInstance().mainThread().execute(new Runnable() {//Run the toast from the UI thread, instead of the non-UI thread
                    //executing the callback to avoid an exception.
                    @Override
                    public void run() {
                        searchProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Search failed. Try again.", Toast.LENGTH_LONG).show();
                    }
                });
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
                           searchProgressBar.setVisibility(View.GONE);
                           recyclerAdapter.notifyDataSetChanged();
                       }
                   });
                }
            }
        };
        return songCallback;
    }
}
