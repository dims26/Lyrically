package com.dims.lyrically

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.dims.lyrically.database.LyricDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_nav.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var menuItem: MenuItem
    private lateinit var db: LyricDatabase
    private lateinit var viewModel: HomeViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        db = LyricDatabase.getDbInstance(requireContext())
        val factory = ViewModelFactory(Repository(db))
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        val searchButton = view.findViewById<FloatingActionButton>(R.id.searchButton)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment)
                as NavHostFragment
        //set navHostFragment's controller as the controller of the bottomNavigationView.
        //The Ids of the bottomNav's menu items point to the corresponding fragment ids in
        // the nav_graph_home
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(bottomNavigationView, NavHostFragment.findNavController(navHostFragment))
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        //custom setup to change only toolbar title when new screen is loaded
        navHostFragment.navController.addOnDestinationChangedListener{ _, destination, _ ->
            toolbar.title = destination.label
            if (this::menuItem.isInitialized)
                menuItem.isVisible = (destination as FragmentNavigator.Destination).className == HistFragment::class.java.name
            if((destination as FragmentNavigator.Destination).className == FavFragment::class.java.name){
                searchButton.show() }
            else{ searchButton.hide() }
        }
        searchButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            NavHostFragment.findNavController(nav_container).navigate(action)
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.history_menu, menu)
        menuItem = menu.findItem(R.id.action_clear_history)
        menuItem.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clear_history) {
            val clearHistoryDialog = AlertDialog.Builder(activity)
            clearHistoryDialog.setPositiveButton("CLEAR") { _, _ ->
                viewModel.clearHistory()
            }
            clearHistoryDialog.setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
            clearHistoryDialog.setMessage("Clear history?").show()
        }
        return true
    }
}