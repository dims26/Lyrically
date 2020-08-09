package com.dims.lyrically.screens.search

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
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
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.databinding.FragmentSearchBinding
import com.dims.lyrically.listeners.RecyclerViewClickListener
import com.dims.lyrically.listeners.RecyclerViewTouchListener
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.utils.LoadState.*
import com.dims.lyrically.utils.LyricDataProvider
import com.dims.lyrically.utils.ViewModelFactory
import com.dims.lyrically.utils.picasso
import kotlinx.android.synthetic.main.activity_nav.*
import kotlinx.android.synthetic.main.fragment_search.*
import okhttp3.OkHttpClient

class SearchFragment : Fragment() {
    private val mAdapter = SearchRecyclerAdapter(picasso)
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchRecycler: RecyclerView
    private lateinit var db: LyricDatabase
    private lateinit var viewModel: SearchViewModel
    private lateinit var searchProgressBar: ProgressBar
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navBackImageView: ImageView
    private lateinit var searchView: SearchView

    private val okHttpClient = OkHttpClient()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        searchProgressBar = binding.root.findViewById(R.id.search_progressBar)
        searchProgressBar.visibility = View.GONE



        toolbar = binding.root.findViewById(R.id.toolbar)

        searchView = toolbar.findViewById(R.id.searchView)
        searchView.onActionViewExpanded()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrBlank()) viewModel.search(query, LyricDataProvider(requireActivity().applicationContext, okHttpClient))
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {/*Nothing for now*/ return false}
        })

        navBackImageView = toolbar.findViewById(R.id.ivNavBack)
        val navController = NavHostFragment.findNavController(this)
        navBackImageView.setOnClickListener {
            searchView.clearFocus()
            navController.navigateUp()
        }

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
                navController.navigate(action)
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

}