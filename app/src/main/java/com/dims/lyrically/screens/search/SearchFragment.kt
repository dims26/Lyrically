package com.dims.lyrically.screens.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dims.lyrically.ActivityProvider
import com.dims.lyrically.R
import com.dims.lyrically.databinding.FragmentSearchBinding
import com.dims.lyrically.datasources.LyricsAPIDatasource
import com.dims.lyrically.listeners.RecyclerViewClickListener
import com.dims.lyrically.listeners.RecyclerViewTouchListener
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.utils.LoadState
import com.dims.lyrically.utils.LoadState.ERROR
import com.dims.lyrically.utils.LoadState.IDLE
import com.dims.lyrically.utils.LoadState.LOADED
import com.dims.lyrically.utils.LoadState.LOADING
import com.dims.lyrically.utils.ViewModelFactory
import com.dims.lyrically.utils.picasso
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import javax.inject.Inject
import kotlin.reflect.KProperty0

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val mAdapter = SearchRecyclerAdapter(picasso)
    private val mCacheAdapter = SearchRecyclerAdapter(picasso)
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchRecycler: RecyclerView
    private lateinit var cacheRecycler: RecyclerView
    private lateinit var cacheBlock: LinearLayout
    private lateinit var viewModel: SearchViewModel
    private lateinit var searchProgressBar: ProgressBar
    private lateinit var errorImageView: ImageView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navBackImageView: ImageView
    private lateinit var searchView: SearchView
    private lateinit var repo: Repository
    private lateinit var provider: ActivityProvider
    private lateinit var noResult : TextView
    @Inject lateinit var lyricsAPIDatasource: LyricsAPIDatasource

    private val okHttpClient = OkHttpClient()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        searchProgressBar = binding.root.findViewById(R.id.search_progressBar)
        searchProgressBar.visibility = View.GONE
        errorImageView = binding.root.findViewById(R.id.error)
        errorImageView.visibility = View.GONE

        provider = arguments?.get("provider") as ActivityProvider
        repo = provider.getRepo()

        toolbar = binding.root.findViewById(R.id.toolbar)
        //todo try setting toolbar as supportActionBar
        searchView = toolbar.findViewById(R.id.searchView)
        searchView.onActionViewExpanded()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrBlank()) {
                    binding.cacheBlock.visibility = View.GONE
                    searchRecycler.visibility = View.VISIBLE
                    viewModel.search(query, lyricsAPIDatasource)
                }
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(query: String?): Boolean {
                if(!query.isNullOrBlank()) {
                    searchRecycler.visibility = View.GONE
                    binding.cacheBlock.visibility = View.VISIBLE
                    viewModel.searchCache(query)
                }
                return true
            }
        })

        navBackImageView = toolbar.findViewById(R.id.ivNavBack)
        val navController = NavHostFragment.findNavController(this)
        navBackImageView.setOnClickListener {
            searchView.clearFocus()
            navController.navigateUp()
        }

        val factory = ViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(SearchViewModel::class.java)

        searchRecycler = binding.searchRecyclerView
        searchRecycler.layoutManager = LinearLayoutManager(requireContext())
        searchRecycler.adapter = mAdapter

        cacheRecycler = binding.cachesRecyclerView
        cacheRecycler.layoutManager = LinearLayoutManager(requireContext())
        cacheRecycler.adapter = mCacheAdapter

        searchRecycler.setListenerAction(requireContext(), mAdapter, navController)
        cacheRecycler.setListenerAction(requireContext(), mCacheAdapter, navController)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noResult = view.findViewById(R.id.no_result_text)
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.loadingIndicator.createRecyclerObserver(mAdapter, viewModel::songs)
        viewModel.cacheLoadingIndicator.createRecyclerObserver(mCacheAdapter, viewModel::songCaches)
    }

    private fun LiveData<LoadState>.createRecyclerObserver(adapter: SearchRecyclerAdapter, songs: KProperty0<List<Song>>){
        observe(viewLifecycleOwner, Observer {
            when(it){
                IDLE -> {
                    noResult.visibility = View.GONE
                    searchProgressBar.visibility = View.GONE
                    errorImageView.visibility = View.GONE
                }
                LOADING -> {
                    noResult.visibility = View.GONE
                    errorImageView.visibility = View.GONE
                    adapter.submitList(null)
                    searchProgressBar.visibility = View.VISIBLE
                }
                LOADED -> {
                    errorImageView.visibility = View.GONE
                    searchProgressBar.visibility = View.GONE
                    if (songs.get().isEmpty()) {
                        noResult.visibility = View.VISIBLE
                    }
                    else {
                        noResult.visibility = View.GONE
                        adapter.submitList(songs.get())
                    }
                }
                ERROR -> {
                    noResult.visibility = View.GONE
                    adapter.submitList(null)
                    searchProgressBar.visibility = View.GONE
                    errorImageView.visibility = View.VISIBLE
                }
                else -> {}
            }
        })
    }

//    private fun setUpLoadingIndicatorObserver() {
//        viewModel.loadingIndicator.observe(viewLifecycleOwner, Observer {
//            when(it){
//                IDLE -> {
//                    noResult.visibility = View.GONE
//                    searchProgressBar.visibility = View.GONE
//                    error.visibility = View.GONE
//                }
//                LOADING -> {
//                    noResult.visibility = View.GONE
//                    error.visibility = View.GONE
//                    mAdapter.submitList(null)
//                    searchProgressBar.visibility = View.VISIBLE
//                }
//                LOADED -> {
//                    error.visibility = View.GONE
//                    searchProgressBar.visibility = View.GONE
//                    if (viewModel.songs.isEmpty()) {
//                        noResult.visibility = View.VISIBLE
//                    }
//                    else {
//                        noResult.visibility = View.GONE
//                        mAdapter.submitList(viewModel.songs)
//                    }
//                }
//                ERROR -> {
//                    noResult.visibility = View.GONE
//                    mAdapter.submitList(null)
//                    searchProgressBar.visibility = View.GONE
//                    error.visibility = View.VISIBLE
//                }
//                else -> {}
//            }
//        })
//    }

    private fun RecyclerView.setListenerAction(context: Context, adapter: SearchRecyclerAdapter, navController: NavController){
        addOnItemTouchListener(RecyclerViewTouchListener(context, this, object : RecyclerViewClickListener {
            override fun onClick(view: View?, position: Int) {
                val action =
                        SearchFragmentDirections.actionSearchFragmentToDetailFragment(adapter.currentList[position], provider)
                navController.navigate(action)
            }

            override fun onLongClick(view: View?, position: Int) {/*Nothing*/ }

        }))
    }

}