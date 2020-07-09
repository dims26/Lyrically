package com.dims.lyrically.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dims.lyrically.R
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return AboutPage(requireContext())
                .isRTL(false)
                .setImage(R.mipmap.lyric_launcher)
                .setDescription(getString(R.string.app_description))
                .addGroup("Connect with me:")
                .addEmail("dims95@live.com", "Email")
                .addTwitter("adedimejiES", "Twitter")
                .addGitHub("dims26", "Github")
                .addGroup("Attributions")
                .addItem(Element("Icon made by Freepik from www.flaticon.com", R.mipmap.lyric_launcher)
                        .setIntent(getIntent("https://www.flaticon.com/free-icon/lyrics_59386?term=lyrics&page=1&position=21")))
                .addItem(Element("Icon made by Pixel perfect from www.flaticon.com", R.drawable.ic_error_icon)
                        .setIntent(getIntent("https://www.flaticon.com/free-icon/error_3161038?term=network%20error&page=2&position=49")))
                .addItem(Element("Other icons by icons8.com", R.drawable.no_image_bubbles)
                        .setIntent(getIntent("https://icons8.com")))
                .create()
    }

    private fun getIntent(url: String) : Intent {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        return i
    }
}