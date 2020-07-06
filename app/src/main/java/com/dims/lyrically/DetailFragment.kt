package com.dims.lyrically

import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.models.Song
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_nav.*

class DetailFragment : Fragment() {
    private lateinit var db: LyricDatabase
    private lateinit var webView: WebView
    private lateinit var detailProgressBar: ProgressBar
    private lateinit var song: Song
    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        db = LyricDatabase.getDbInstance(requireContext())

        song = arguments?.getSerializable("song") as Song

        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        NavigationUI.setupWithNavController(toolbar, NavHostFragment.findNavController(nav_container))
        toolbar.title = song.title

        detailProgressBar = view.findViewById(R.id.detail_progressBar)
        detailProgressBar.visibility = View.GONE
        detailProgressBar.max = 100

        val factory = ViewModelFactory(Repository(db))
        viewModel = ViewModelProvider(this, factory).get(DetailViewModel::class.java)

        webView = view.findViewById(R.id.lyrics_webView)
        webView.webViewClient = viewModel.getLyricWebViewClient()
        webView.webChromeClient = viewModel.getLyricWebChromeClient()
        webView.settings.javaScriptEnabled = true
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
        viewModel.snackbar.observe(viewLifecycleOwner, Observer {
            if (it == 1)
                Snackbar.make(requireView(), "Added to favourites", Snackbar.LENGTH_SHORT).show()
            else if (it == 2)
                Snackbar.make(requireView(), "Removed from favourites", Snackbar.LENGTH_SHORT).show()
        })
        viewModel.addToHistory(song)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.lyric_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favourite_lyric)
            viewModel.toggleFavourite(song)

        return true
    }


}

