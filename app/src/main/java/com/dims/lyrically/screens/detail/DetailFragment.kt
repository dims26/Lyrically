package com.dims.lyrically.screens.detail

import android.os.Bundle
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dims.lyrically.R
import com.dims.lyrically.utils.ViewModelFactory
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import kotlinx.android.synthetic.main.activity_nav.*


class DetailFragment : Fragment() {
    private lateinit var favMenuItem: MenuItem
    private lateinit var db: LyricDatabase
    private lateinit var webView: WebView
    private lateinit var detailProgressBar: ProgressBar
    private lateinit var song: Song
    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        setHasOptionsMenu(true)

        db = LyricDatabase.getDbInstance(requireContext())

        song = arguments?.getSerializable("song") as Song

        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, NavHostFragment.findNavController(nav_container))
        toolbar.title = song.title

        detailProgressBar = view.findViewById(R.id.detail_progressBar)
        detailProgressBar.max = 100

        val factory = ViewModelFactory(Repository(db))
        viewModel = ViewModelProvider(this, factory).get(DetailViewModel::class.java)

        webView = view.findViewById(R.id.lyrics_webView)
        webView.webViewClient = viewModel.getLyricWebViewClient()
        webView.webChromeClient = viewModel.getLyricWebChromeClient()
        //apply settings
        webView.settings.setAppCachePath(requireContext().cacheDir.absolutePath)
        webView.settings.setAppCacheEnabled(true)
        webView.settings.allowFileAccess = true
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT

        val isAvailable = viewModel.isNetworkAvailable(requireContext())
        if (!isAvailable){
            webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }

        webView.loadUrl(song.url)

        val refresher = view.findViewById<SwipeRefreshLayout>(R.id.webView_refresher)
        refresher.setOnRefreshListener {
            webView.reload()
            refresher.isRefreshing = false
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.progress.observe(viewLifecycleOwner, Observer {
            detailProgressBar.progress = it
        })
        viewModel.isVisible.observe(viewLifecycleOwner, Observer {
            if (it) detailProgressBar.visibility = View.VISIBLE
            else detailProgressBar.visibility = View.GONE
        })
        //observe db for data
        viewModel.setupDbLiveData(song)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.lyric_detail_menu, menu)
        favMenuItem = menu.findItem(R.id.favourite_lyric)
        viewModel.favourites.observe(viewLifecycleOwner, Observer {
            with(song){
                if (it.contains(Favourites(id, fullTitle, title, songArtImageThumbnailUrl,
                                url, titleWithFeatured, artistName))){
                    favMenuItem.icon = requireContext().getDrawable(R.drawable.round_favorite_24)}
                else favMenuItem.icon = requireContext().getDrawable(R.drawable.round_favorite_border_24)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favourite_lyric)
            viewModel.toggleFavourite(song)

        return true
    }


}

