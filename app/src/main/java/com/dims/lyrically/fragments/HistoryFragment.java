package com.dims.lyrically.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dims.lyrically.AppExecutors;
import com.dims.lyrically.R;
import com.dims.lyrically.activities.LyricDetailActivity;
import com.dims.lyrically.adapters.HistoryListItemRecyclerAdapter;
import com.dims.lyrically.database.History;
import com.dims.lyrically.database.LyricDatabase;
import com.dims.lyrically.listeners.RecyclerViewClickListener;
import com.dims.lyrically.listeners.RecyclerViewTouchListener;
import com.dims.lyrically.models.Song;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LyricDatabase db;
    private HistoryListItemRecyclerAdapter mAdapter = new HistoryListItemRecyclerAdapter();

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        setHasOptionsMenu(true);

        layoutManager = new LinearLayoutManager(getActivity());

        db = LyricDatabase.getDbInstance(getActivity());

        recyclerView = view.findViewById(R.id.history_recycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), LyricDetailActivity.class);
                History history = mAdapter.mHistory.get(position);
                Song song = new Song(history.getFullTitle(), history.getTitle(), history.getSongArtImageThumbnailUrl(),
                        history.getUrl(), history.getTitleWithFeatured(), history.getId(), history.getArtistName());
                intent.putExtra("song", song);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(getActivity());
                deleteDialogBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final History history = mAdapter.mHistory.get(position);
                        mAdapter.mHistory.remove(history);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                db.historyDao().deleteFromHistory(history);
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
                deleteDialogBuilder.setMessage("Delete \"" + mAdapter.mHistory.get(position).getTitle() +
                        "\" from history?");
                deleteDialogBuilder.create().show();
            }
        }));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mAdapter.mHistory.clear();
                mAdapter.mHistory.addAll(db.historyDao().getHistory());
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.history_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_clear_history){
            AlertDialog.Builder clearHistoryDialog = new AlertDialog.Builder(getActivity());
            clearHistoryDialog.setMessage("Clear history?");
            clearHistoryDialog.setPositiveButton("CLEAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAdapter.mHistory.clear();
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            db.historyDao().clearHistory();
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
            clearHistoryDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            clearHistoryDialog.create().show();
        }
        return true;
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
