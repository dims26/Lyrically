package com.dims.lyrically.screens.history

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.databinding.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dims.lyrically.*
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.databinding.FragmentHistBinding
import com.dims.lyrically.listeners.RecyclerViewClickListener
import com.dims.lyrically.listeners.RecyclerViewTouchListener
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.home.HomeFragmentDirections
import com.dims.lyrically.utils.ViewModelFactory

class HistFragment : Fragment() {

    private val mAdapter = HistoryRecyclerAdapter()
    private lateinit var binding: FragmentHistBinding
    private lateinit var db: LyricDatabase
    private lateinit var viewModel: HistViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hist, container, false)
        binding.lifecycleOwner = this

        db = LyricDatabase.getDbInstance(requireContext())

        //initialize viewModel, get data and update adapter list
        val factory = ViewModelFactory(Repository(db))
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
                        HomeFragmentDirections.actionHomeFragmentToDetailFragment(song)
                //Go up two steps to HomeFragment to navigate
                NavHostFragment.findNavController(requireParentFragment().requireParentFragment()).navigate(action)
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