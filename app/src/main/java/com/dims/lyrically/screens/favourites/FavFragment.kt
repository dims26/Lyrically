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
import androidx.navigation.NavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dims.lyrically.ActivityProvider
import com.dims.lyrically.R
import com.dims.lyrically.databinding.FragmentFavBinding
import com.dims.lyrically.listeners.RecyclerViewClickListener
import com.dims.lyrically.listeners.RecyclerViewTouchListener
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.home.HomeFragmentDirections
import com.dims.lyrically.screens.home.HomeProvider
import com.dims.lyrically.utils.ViewModelFactory
import com.dims.lyrically.utils.picasso
import org.jetbrains.annotations.TestOnly


class FavFragment : Fragment() {
    private val mAdapter = FavouritesRecyclerAdapter(picasso)
    private lateinit var binding: FragmentFavBinding
    private lateinit var repo: Repository
    private lateinit var homeNavController: NavController
    private lateinit var provider: HomeProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fav, container, false)
        binding.lifecycleOwner = this

        provider = arguments?.getParcelable("provider")!!
        repo = provider.getRepo()
        homeNavController = provider.getHomeNavController()

        //initialize viewModel, and setup recyclerView
        val factory = ViewModelFactory(repo)
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
                        HomeFragmentDirections.actionHomeFragmentToDetailFragment(song, provider as ActivityProvider)
                homeNavController.navigate(action)
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

    @TestOnly
    fun populateAdapter(favourites: List<Favourites>){
        mAdapter.submitList(favourites)
    }

    private fun getNumberOfColumns(): Int {
        val cardItem = View.inflate(requireContext(), R.layout.card_item, null)
        cardItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val cardWidth = cardItem.measuredWidth
        val displayWidth = resources.displayMetrics.widthPixels
        val isCloseToNextInt = (displayWidth % cardWidth).toDouble()/cardWidth >= .85
        return if(isCloseToNextInt) (displayWidth / cardWidth + 1) else displayWidth/cardWidth
    }
}