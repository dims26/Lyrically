package com.dims.lyrically.screens.detail

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dims.lyrically.ActivityProvider
import com.dims.lyrically.R
import com.dims.lyrically.models.Favourites
import com.dims.lyrically.models.Song
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.utils.ViewModelFactory


class DetailFragment : Fragment() {
    private lateinit var favMenuItem: MenuItem
    private lateinit var repo: Repository
    private lateinit var webView: WebView
    private lateinit var detailProgressBar: ProgressBar
    private lateinit var song: Song
    private lateinit var viewModel: DetailViewModel
    private lateinit var provider: ActivityProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        setHasOptionsMenu(true)


        provider = arguments?.get("provider")!! as ActivityProvider
        repo = provider.getRepo()
        song = arguments?.getSerializable("song") as Song

        retainInstance = true

        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        provider.setToolbarAsActionbar(toolbar)
        NavigationUI.setupWithNavController(toolbar, NavHostFragment.findNavController(provider.getActivityNavContainer()!!))
        toolbar.title = song.title

        detailProgressBar = view.findViewById(R.id.detail_progressBar)
        detailProgressBar.max = 100

        val factory = ViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(DetailViewModel::class.java)

        webView = view.findViewById(R.id.lyrics_webView)
        webView.webViewClient = viewModel.getLyricWebViewClient()
        webView.webChromeClient = viewModel.getLyricWebChromeClient()
        //apply settings
        webView.settings.allowFileAccess = true
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        //testing
//        webView.settings.domStorageEnabled = true

        val cm = ContextCompat.getSystemService(requireContext(), ConnectivityManager::class.java)
        val isAvailable = viewModel.isNetworkAvailable(cm)
        if (!isAvailable){
            webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }

        if (savedInstanceState == null) webView.loadUrl(song.url)
        else webView.restoreState(savedInstanceState)

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
        viewModel.updateHistory(song)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.lyric_detail_menu, menu)
        favMenuItem = menu.findItem(R.id.favourite_lyric)
        viewModel.favourites.observe(viewLifecycleOwner, Observer {
            with(song){
                if (it.contains(Favourites(id, fullTitle, title, songArtImageThumbnailUrl,
                                url, titleWithFeatured, artistName))){
                    favMenuItem.icon = AppCompatResources.getDrawable(requireContext(), R.drawable.round_favorite_24)}
                else favMenuItem.icon = AppCompatResources.getDrawable(requireContext(), R.drawable.round_favorite_border_24)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favourite_lyric)
            viewModel.toggleFavourite(song)

        return true
    }
}

