package com.dims.lyrically.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.dims.lyrically.AppExecutors;
import com.dims.lyrically.R;
import com.dims.lyrically.activities.LyricDetailActivity;
import com.dims.lyrically.adapters.FavouritesCardItemRecyclerAdapter;
import com.dims.lyrically.database.Favourites;
import com.dims.lyrically.database.LyricDatabase;
import com.dims.lyrically.listeners.RecyclerViewClickListener;
import com.dims.lyrically.listeners.RecyclerViewTouchListener;
import com.dims.lyrically.models.Song;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LyricDatabase db;
    private FavouritesCardItemRecyclerAdapter mAdapter = new FavouritesCardItemRecyclerAdapter();


    public FavouritesFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mAdapter.mFavourites.clear();
                mAdapter.mFavourites.addAll(Objects.requireNonNull(db.favouritesDao().getFavourites().getValue()));
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        db = LyricDatabase.getDbInstance(getActivity());

        recyclerView = view.findViewById(R.id.favourites_recycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), LyricDetailActivity.class);
                Favourites fav = mAdapter.mFavourites.get(position);
                Song song = new Song(fav.getFullTitle(), fav.getTitle(), fav.getSongArtImageThumbnailUrl(),
                        fav.getUrl(), fav.getTitleWithFeatured(), fav.getId(), fav.getArtistName());
                intent.putExtra("song", song);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(getActivity());
                deleteDialogBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Favourites fav = mAdapter.mFavourites.get(position);
                        mAdapter.mFavourites.remove(fav);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                db.favouritesDao().deleteFromFavourite(fav);
                            }
                        });
                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                deleteDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                deleteDialogBuilder.setMessage("Delete \"" + mAdapter.mFavourites.get(position).getTitle() +
                        "\" from favourites?");
                deleteDialogBuilder.create().show();
            }
        }));
        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
