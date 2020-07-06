package com.dims.lyrically

import android.app.SearchManager
import android.app.VoiceInteractor
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.databinding.FragmentSearchBinding
import com.dims.lyrically.listeners.RecyclerViewClickListener
import com.dims.lyrically.listeners.RecyclerViewTouchListener
import com.dims.lyrically.LoadState.*
import kotlinx.android.synthetic.main.activity_nav.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private val mAdapter = SearchRecyclerAdapter()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchRecycler: RecyclerView
    private lateinit var db: LyricDatabase
    private lateinit var viewModel: SearchViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this

        search_progressBar.visibility = View.GONE

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
        viewModel.loadingIndicator.observe(viewLifecycleOwner, Observer {
            when(it){
                IDLE -> {
                    search_progressBar.visibility = View.GONE
                    error.visibility = View.GONE
                }
                LOADING -> {
                    error.visibility = View.GONE
                    mAdapter.submitList(null)
                    search_progressBar.visibility = View.VISIBLE
                }
                LOADED -> {
                    error.visibility = View.GONE
                    search_progressBar.visibility = View.GONE
                    mAdapter.submitList(viewModel.songs)
                }
                ERROR -> {
                    mAdapter.submitList(null)
                    search_progressBar.visibility = View.GONE
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

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrBlank()) viewModel.search(query, requireContext())
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {/*Nothing for now*/ return false}
        })

    }

    private fun initSearchView(searchItem: MenuItem): androidx.appcompat.widget.SearchView {
        val searchManager = getSystemService(requireContext(), SearchManager::class.java)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.setSearchableInfo(searchManager!!.getSearchableInfo(requireActivity().componentName))
        searchView.setIconifiedByDefault(false)

        // Get the Linear Layout
        val searchEditFrame = searchView.findViewById<LinearLayout>(R.id.search_edit_frame)
        // Get the associated LayoutParams and set the leftMargin
        (searchEditFrame.layoutParams as LinearLayout.LayoutParams).leftMargin = 0

        //Remove search icon to the left of the search edit text
        val searchViewIcon = searchView.findViewById<ImageView>(R.id.search_mag_icon)
        val linearLayoutSearchView = searchViewIcon.parent as ViewGroup
        linearLayoutSearchView.removeView(searchViewIcon)
        return searchView
    }
}