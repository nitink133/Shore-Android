package com.theshoremedia.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.theshoremedia.R
import com.theshoremedia.databinding.ActivityMainBinding
import com.theshoremedia.fragments.FactsCheckListFragment
import com.theshoremedia.modules.navigation.adapter.NavigationDrawerAdapter
import com.theshoremedia.modules.navigation.model.NavigationDataModel
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.KeyBoardManager
import com.theshoremedia.utils.extensions.makeVisible
import com.theshoremedia.utils.extensions.validateNoDataView
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.layout_navigation_view.*
import kotlinx.android.synthetic.main.layout_recycler_view.*
import java.util.*

class MainActivity : BaseActivity() {
    private var currentTab: String = ""

    private lateinit var binding: ActivityMainBinding
    private var titleStack: Stack<String?> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)


        // initializing navigation menu
        setUpNavigationView()
        if (currentTab.isEmpty()) {
            loadHomeFragment()
        }
    }


    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private fun loadHomeFragment() {
        loadFragment(
            fragment = FactsCheckListFragment(),
            animateFragment = false
        )
        //Closing drawer on item click
        binding.drawerLayout.closeDrawers()

        // refresh toolbar menu
        invalidateOptionsMenu()
    }


    private fun setUpNavigationView() {
        val actionBarDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.openDrawer,
            R.string.closeDrawer
        ) {
            //no -use
        }


        //Setting the actionbarToggle to drawer layout
        binding.drawerLayout.setDrawerListener(actionBarDrawerToggle)

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState()

        ivCloseDrawer?.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        val navigationItems = getNavigationItems()
        val adapter = NavigationDrawerAdapter(items = navigationItems) { _, title ->
            loadFragment(fragment = FactsCheckListFragment(), title = title)
        }
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        recyclerView?.validateNoDataView()

        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.frame)
                ?: return@addOnBackStackChangedListener
            if (titleStack.isEmpty()) return@addOnBackStackChangedListener

            setTitle(
                fragment,
                titleStack.peek()
            )
        }

    }

    private fun getNavigationItems(): ArrayList<NavigationDataModel> {
        val items = arrayListOf<NavigationDataModel>()
        val labels = resources.getStringArray(R.array.array_navigation_labels)
        val titles = resources.getStringArray(R.array.array_navigation_titles)
        for (label in labels) {
            items.add(
                NavigationDataModel(
                    label = label,
                    toolbarTitle = titles[labels.indexOf(label)]
                )
            )
        }
        return items

    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
            return
        }

        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            titleStack.pop()
        } else {
            clearBackStack(this)
            finish()
        }
    }


    override fun loadFragment(
        fragment: Fragment,
        title: String?,
        addToBackStack: Boolean,
        animateFragment: Boolean,
        toAddOrReplace: Int
    ) {
        binding.drawerLayout.closeDrawers()
        setTitle(fragment, title)
        titleStack.push(title)
        KeyBoardManager.hideKeyboard(this)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (animateFragment) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }
        if (toAddOrReplace == AppConstants.FragmentConstants.REPLACE) {
            fragmentTransaction.replace(R.id.frame, fragment, currentTab)
        } else if (toAddOrReplace == AppConstants.FragmentConstants.ADD) {
            fragmentTransaction.add(R.id.frame, fragment, currentTab)
        }
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(currentTab)
        }
        fragmentTransaction.commit()
    }

    private fun setTitle(fragment: Fragment, title: String?) {
        currentTab = fragment::class.java.simpleName
        binding.layoutAppBarMain.findViewById<ImageView>(R.id.ivShoreIcon)
            .makeVisible(isVisible = title.isNullOrEmpty())
        binding.layoutAppBarMain.findViewById<AppCompatTextView>(R.id.tvToolbarTitle)
            .makeVisible(isVisible = !title.isNullOrEmpty())
        binding.layoutAppBarMain.findViewById<AppCompatTextView>(R.id.tvToolbarTitle)
            .text = title

    }


}