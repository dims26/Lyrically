package com.dims.lyrically.screens.history

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.dims.lyrically.ActivityProvider
import com.dims.lyrically.R
import com.dims.lyrically.databinding.FragmentHistBinding
import com.dims.lyrically.listeners.RecyclerViewClickListener
import com.dims.lyrically.listeners.RecyclerViewTouchListener
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.home.HomeFragmentDirections
import com.dims.lyrically.screens.home.HomeProvider
import com.dims.lyrically.utils.ViewModelFactory
import com.dims.lyrically.utils.picasso


class HistFragment : Fragment(){

    private val mAdapter = HistoryRecyclerAdapter(picasso)
    private lateinit var binding: FragmentHistBinding
    private lateinit var viewModel: HistViewModel
    private lateinit var provider: HomeProvider
    private lateinit var repo: Repository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hist, container, false)
        binding.lifecycleOwner = this

        provider = arguments?.getParcelable("provider")!!
        repo = provider.getRepo()

        //initialize viewModel, and setup recycler
        val factory = ViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(HistViewModel::class.java)
        binding.histViewModel = viewModel
        val histRecycler = binding.histRecycler
        histRecycler.adapter = mAdapter
        histRecycler.layoutManager = LinearLayoutManager(requireContext())

        val errorIndicator = binding.errorIndicator

        viewModel.history.observe(viewLifecycleOwner, Observer {
            errorIndicator.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            it?.let {
                mAdapter.submitList(it)
            }
        })

        histRecycler.addOnItemTouchListener(RecyclerViewTouchListener(activity, histRecycler, object : RecyclerViewClickListener{
            override fun onClick(view: View?, position: Int) {
                val song: Song
                with(mAdapter.currentList[position]){
                    song = Song(fullTitle, title, songArtImageThumbnailUrl, url, titleWithFeatured, id, artistName)
                }
                val action =
                        HomeFragmentDirections.actionHomeFragmentToDetailFragment(song, provider as ActivityProvider)
                //Go up two steps to HomeFragment to navigate
                provider.getHomeNavController()?.navigate(action)
            }
            override fun onLongClick(view: View?, position: Int) {
                AlertDialog.Builder(activity)
                        .setPositiveButton("DELETE") { _, _ ->
                            viewModel.delete(mAdapter.currentList[position]) }
                        .setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss()}
                        .setMessage("Delete ${mAdapter.currentList[position].title} from history?")
                        .show()
            }
        }))

        return binding.root
    }
}