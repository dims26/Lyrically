package com.dims.lyrically.screens.search

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dims.lyrically.R
import com.dims.lyrically.utils.LoadState.*
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.databinding.FragmentSearchBinding
import com.dims.lyrically.listeners.RecyclerViewClickListener
import com.dims.lyrically.listeners.RecyclerViewTouchListener
import com.dims.lyrically.utils.LyricDataProvider
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.utils.ViewModelFactory
import kotlinx.android.synthetic.main.activity_nav.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private val mAdapter = SearchRecyclerAdapter()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchRecycler: RecyclerView
    private lateinit var db: LyricDatabase
    private lateinit var viewModel: SearchViewModel
    private lateinit var searchProgressBar: ProgressBar
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        searchProgressBar = binding.root.findViewById(R.id.search_progressBar)
        searchProgressBar.visibility = View.GONE

        toolbar = binding.root.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, NavHostFragment.findNavController(nav_container))

        db = LyricDatabase.getDbInstance(requireContext())
        val factory = ViewModelFactory(Repository(db))
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)

        searchRecycler = binding.searchRecyclerView
        searchRecycler.layoutManager = LinearLayoutManager(requireContext())
        searchRecycler.adapter = mAdapter

        searchRecycler.addOnItemTouchListener(RecyclerViewTouchListener(requireContext(), searchRecycler, object : RecyclerViewClickListener {
            override fun onClick(view: View?, position: Int) {
                val action =
                        SearchFragmentDirections.actionSearchFragmentToDetailFragment(mAdapter.currentList[position])
                NavHostFragment.findNavController(nav_container).navigate(action)
            }
            override fun onLongClick(view: View?, position: Int) {/*Nothing*/ }
        }))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val noResult = view.findViewById<TextView>(R.id.no_result_text)
        viewModel.loadingIndicator.observe(viewLifecycleOwner, Observer {
            when(it){
                IDLE -> {
                    noResult.visibility = View.GONE
                    searchProgressBar.visibility = View.GONE
                    error.visibility = View.GONE
                }
                LOADING -> {
                    noResult.visibility = View.GONE
                    error.visibility = View.GONE
                    mAdapter.submitList(null)
                    searchProgressBar.visibility = View.VISIBLE
                }
                LOADED -> {
                    error.visibility = View.GONE
                    searchProgressBar.visibility = View.GONE
                    if (viewModel.songs.isEmpty()) {
                        noResult.visibility = View.VISIBLE
                    }
                    else {
                        view.findViewById<TextView>(R.id.no_result_text).visibility = View.GONE
                        mAdapter.submitList(viewModel.songs)
                    }
                }
                ERROR -> {
                    noResult.visibility = View.GONE
                    mAdapter.submitList(null)
                    searchProgressBar.visibility = View.GONE
                    error.visibility = View.VISIBLE
                }
                else -> {}
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)

        val searchView = initSearchView(searchItem)
        searchView.queryHint = "Song or Artist name"
        searchView.requestFocus() //sets the focus on searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrBlank()) viewModel.search(query, LyricDataProvider(requireActivity().applicationContext))
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {/*Nothing for now*/ return false}
        })
    }

    private fun initSearchView(searchItem: MenuItem): SearchView {
        val searchView = searchItem.actionView as SearchView
        searchView.setIconifiedByDefault(false)
        //request focus
        searchView.isIconified = false
        searchView.isFocusable = true

        // Get the associated LayoutParams and set the leftMargin
        val searchEditFrame = searchView.findViewById<LinearLayout>(R.id.search_edit_frame)
        (searchEditFrame.layoutParams as LinearLayout.LayoutParams).leftMargin = 0

        //Remove search icon to the left of the search edit text
        val searchViewIcon = searchView.findViewById<ImageView>(R.id.search_mag_icon)
        val linearLayoutSearchView = searchViewIcon.parent as ViewGroup
        linearLayoutSearchView.removeView(searchViewIcon)
        return searchView
    }
}