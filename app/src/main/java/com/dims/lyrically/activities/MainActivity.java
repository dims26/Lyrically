package com.dims.lyrically.activities;

import android.content.Intent;
import android.os.Bundle;

import com.dims.lyrically.R;
import com.dims.lyrically.fragments.FavouritesFragment;
import com.dims.lyrically.fragments.HistoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private FavouritesFragment favouritesFragment;
    private HistoryFragment historyFragment;
    private FloatingActionButton searchButton;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.addToBackStack(null);//prevents crashing from quick switching, check documentation for understanding

            switch (item.getItemId()) {
                case R.id.favouritesFragment:
                    transaction.replace(R.id.fragment_container, favouritesFragment);//Change method to add() if it crashes
                    transaction.commit();
                    return true;
                case R.id.historyFragment:
                    transaction.replace(R.id.fragment_container, historyFragment);//Change method to add() if it crashes
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);//Transition back to regular theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Might need to move the initializations out of the method
        favouritesFragment = new FavouritesFragment();
        historyFragment = new HistoryFragment();

        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, favouritesFragment);//Change method to add() if it crashes
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof FavouritesFragment){
            finish();
        }else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, favouritesFragment);
            transaction.commit();

            navView.setSelectedItemId(R.id.favouritesFragment);
        }
    }
}
