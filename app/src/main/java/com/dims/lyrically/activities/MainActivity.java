package com.dims.lyrically.activities;

import android.os.Bundle;

import com.dims.lyrically.R;
import com.dims.lyrically.fragments.FavouritesFragment;
import com.dims.lyrically.fragments.HistoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private FavouritesFragment favouritesFragment;
    private HistoryFragment historyFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.addToBackStack(null);//prevents crashing from quick switching, check documentation for understanding

            switch (item.getItemId()) {
                case R.id.navigation_favourites:
                    transaction.replace(R.id.fragment_container, favouritesFragment);//Change method to add() if it crashes
                    transaction.commit();
                    return true;
                case R.id.navigation_history:
                    transaction.replace(R.id.fragment_container, historyFragment);//Change method to add() if it crashes
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Might need to move the initializations out of the method
        favouritesFragment = new FavouritesFragment();
        historyFragment = new HistoryFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, favouritesFragment);//Change method to add() if it crashes
        transaction.commit();
    }

}
