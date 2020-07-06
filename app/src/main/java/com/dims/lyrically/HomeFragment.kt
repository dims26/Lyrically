package com.dims.lyrically

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment)
                as NavHostFragment
        //set navHostFragment's controller as the controller of the bottomNavigationView.
        //The Ids of the bottomNav's menu items point to the corresponding fragment ids in
        // the nav_graph_home
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(bottomNavigationView, NavHostFragment.findNavController(navHostFragment))
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        //custom setup to change only toolbar title when new screen is loaded
        navHostFragment.navController.addOnDestinationChangedListener{ _, destination, _ ->
            toolbar.title = destination.label }
        return view
    }
}