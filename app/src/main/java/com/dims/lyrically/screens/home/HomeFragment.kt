package com.dims.lyrically.screens.home

import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.dims.lyrically.ActivityProvider
import com.dims.lyrically.R
import com.dims.lyrically.database.LyricDatabase
import com.dims.lyrically.repository.Repository
import com.dims.lyrically.screens.favourites.FavFragment
import com.dims.lyrically.screens.history.HistFragment
import com.dims.lyrically.utils.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_nav.*

@Parcelize
class HomeFragment : Fragment(), HomeProvider {
    @IgnoredOnParcel
    private var currentFragmentMenuId = 0
    @IgnoredOnParcel
    private lateinit var menuItem: MenuItem
    @IgnoredOnParcel
    private lateinit var db: LyricDatabase
    @IgnoredOnParcel
    private lateinit var viewModel: HomeViewModel
    @IgnoredOnParcel
    private lateinit var navController: NavController
    @IgnoredOnParcel
    private lateinit var provider: ActivityProvider

    override fun getHomeNavController(): NavController = NavHostFragment.findNavController(this)
    override fun getRepo(): Repository = provider.getRepo()
    override fun setToolbarAsActionbar(toolbar: Toolbar) { provider.setToolbarAsActionbar(toolbar) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        provider = arguments?.get("provider") as ActivityProvider
        val factory = ViewModelFactory(provider.getRepo())
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        val searchButton = view.findViewById<FloatingActionButton>(R.id.searchButton)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragment)
                as NavHostFragment
        navController = navHostFragment.navController

        //Manually create nav_graph_home and add default argument for favFragment
        //todo when doing same for nav_graph, repo won't be created here but passed in from nav_activity
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.nav_graph_home)
        val providerArg = NavArgument.Builder()
                .setDefaultValue(this as HomeProvider).build()
        graph.addArgument("provider", providerArg)
        navController.graph = graph

        //set navHostFragment's controller as the controller of the bottomNavigationView.
        //The Ids of the bottomNav's menu items point to the corresponding fragment ids in
        // the nav_graph_home. todo Add the repo as dependency for homeFragment
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        //override default bottomNav menu actions and pass args for to fragments.
        // This is for user clicks on bottomNav, for passing args on initial load,
        // initialize the nav graph manually and pass in the args(see above)
        bottomNavigationView.setOnNavigationItemSelectedListener{ menuItem ->
            if (menuItem.itemId == currentFragmentMenuId){/**/}
            else{
                when (menuItem.itemId) {
                    R.id.favFragment, R.id.histFragment -> {
                        val bundle = Bundle()
                        bundle.putParcelable("provider", (this as HomeProvider))
                        currentFragmentMenuId = menuItem.itemId
                        navController.navigate(menuItem.itemId, bundle)
                    }
                    else -> {
                        currentFragmentMenuId = menuItem.itemId
                        navController.navigate(menuItem.itemId)
                    }
                }
            }
            true
        }

        //setup the toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        provider.setToolbarAsActionbar(toolbar)

        //custom setup to change only toolbar title when new screen is loaded
        navHostFragment.navController.addOnDestinationChangedListener{ _, destination, _ ->
            toolbar.title = destination.label
            if (this::menuItem.isInitialized)
                menuItem.isVisible =
                        (destination as FragmentNavigator.Destination).className ==
                                HistFragment::class.java.name

            if((destination as FragmentNavigator.Destination).className == FavFragment::class.java.name){
                searchButton.show() }
            else{ searchButton.hide() }
        }

        searchButton.setOnClickListener {
            val action =
                    HomeFragmentDirections.actionHomeFragmentToSearchFragment(provider)
            //this controller isn't navController but the controller of the fragment container
            //in nav activity
            NavHostFragment.findNavController(this).navigate(action)
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
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
        } else if (item.itemId == R.id.action_about){
            val action = HomeFragmentDirections.actionHomeFragmentToAboutFragment()
            NavHostFragment.findNavController(nav_container).navigate(action)
        }
        return true
    }
}