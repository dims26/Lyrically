package com.dims.lyrically

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.databinding.FragmentFavBinding
import com.dims.lyrically.listeners.RecyclerViewClickListener
import com.dims.lyrically.listeners.RecyclerViewTouchListener
import com.dims.lyrically.models.Song
import kotlinx.android.synthetic.main.activity_nav.*
import kotlinx.android.synthetic.main.fragment_fav.*

class FavFragment : Fragment() {
    private val mAdapter = FavouritesRecyclerAdapter()
    private lateinit var binding: FragmentFavBinding
    private lateinit var db: LyricDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_fav, container, false)
        binding.lifecycleOwner = this

        db = LyricDatabase.getDbInstance(requireContext())

        //initialize viewModel, get data and update adapter list
        val factory = ViewModelFactory(Repository(db))
        val viewModel = ViewModelProvider(this, factory).get(FavViewModel::class.java)
        binding.favViewModel = viewModel
        val favRecycler = binding.favRecycler
        favRecycler.adapter = mAdapter
        favRecycler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        viewModel.favourites.observe(viewLifecycleOwner, Observer {
            it?.let {
                mAdapter.submitList(it)
            }
        })
        favRecycler.addOnItemTouchListener(RecyclerViewTouchListener(activity, fav_recycler, object: RecyclerViewClickListener{
            override fun onClick(view: View?, position: Int) {
                val song: Song
                with(mAdapter.currentList[position]){
                    song = Song(fullTitle, title, songArtImageThumbnailUrl, url, titleWithFeatured, id, artistName)}
                val action =
                        HomeFragmentDirections.actionHomeFragmentToDetailFragment(song)
                NavHostFragment.findNavController(nav_container).navigate(action)
            }
            override fun onLongClick(view: View?, position: Int) {
                AlertDialog.Builder(activity)
                        .setPositiveButton("DELETE") { _, _ ->
                            viewModel.delete(mAdapter.currentList[position]) }
                        .setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss()}
                        .setMessage("Delete ${mAdapter.currentList[position].title} from favourites?")
                        .show()
            }
        }))
        return binding.root
    }
}