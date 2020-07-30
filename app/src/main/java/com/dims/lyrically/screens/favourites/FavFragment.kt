package com.dims.lyrically.screens.favourites

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
import com.dims.lyrically.*
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.databinding.FragmentFavBinding
import com.dims.lyrically.listeners.RecyclerViewClickListener
import com.dims.lyrically.listeners.RecyclerViewTouchListener
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.home.HomeFragmentDirections
import com.dims.lyrically.utils.ViewModelFactory
import com.dims.lyrically.utils.picasso


class FavFragment : Fragment() {
    private val mAdapter = FavouritesRecyclerAdapter(picasso)
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
        favRecycler.layoutManager = StaggeredGridLayoutManager(getNumberOfColumns(), StaggeredGridLayoutManager.VERTICAL)

        val errorIndicator = binding.errorIndicator

        viewModel.favourites.observe(viewLifecycleOwner, Observer {
            errorIndicator.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            it?.let {
                mAdapter.submitList(it)
            }
        })
        favRecycler.addOnItemTouchListener(RecyclerViewTouchListener(activity, favRecycler, object: RecyclerViewClickListener{
            override fun onClick(view: View?, position: Int) {
                val song: Song
                with(mAdapter.currentList[position]){
                    song = Song(fullTitle, title, songArtImageThumbnailUrl, url, titleWithFeatured, id, artistName)}
                val action =
                        HomeFragmentDirections.actionHomeFragmentToDetailFragment(song)
                NavHostFragment.findNavController(requireParentFragment().requireParentFragment()).navigate(action)
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

    private fun getNumberOfColumns(): Int {
        val cardItem = View.inflate(requireContext(), R.layout.card_item, null)
        cardItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val width = cardItem.measuredWidth
        return resources.displayMetrics.widthPixels / width
    }
}