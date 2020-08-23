package com.theshoremedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.theshoremedia.R
import com.theshoremedia.database.helper.FactCheckHistoryDatabaseHelper
import com.theshoremedia.databinding.ActivityMainBinding
import com.theshoremedia.modules.base.BaseActivity
import com.theshoremedia.modules.factchecks.fragments.FactsCheckListFragment
import com.theshoremedia.modules.navigation.adapter.NavigationDrawerAdapter
import com.theshoremedia.modules.navigation.model.NavigationDataModel
import com.theshoremedia.modules.webview.WebViewFragment
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.KeyBoardManager
import com.theshoremedia.utils.ToastUtils
import com.theshoremedia.utils.extensions.makeVisible
import com.theshoremedia.utils.extensions.validateNoDataView
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.layout_navigation_view.*
import kotlinx.android.synthetic.main.layout_recycler_view.*
import java.util.*


class MainActivity : BaseActivity() {
    private var mToolBarNavigationListenerIsRegistered: Boolean = false
    private var currentTab: String = ""

    private lateinit var binding: ActivityMainBinding
    private var titleStack: Stack<String?> = Stack()
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        FactCheckHistoryDatabaseHelper.instance!!.addDummyData(this)

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
            fragment = FactsCheckListFragment.newInstance(),
            animateFragment = false
        )
        //Closing drawer on item click
        binding.drawerLayout.closeDrawers()

        // refresh toolbar menu
        invalidateOptionsMenu()
    }


    private fun setUpNavigationView() {
        actionBarDrawerToggle = object : ActionBarDrawerToggle(
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
        actionBarDrawerToggle?.syncState()

        ivCloseDrawer?.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        val navigationItems = getNavigationItems()
        val adapter = NavigationDrawerAdapter(items = navigationItems) { position, title ->
            onNavigationItemClick(position, title)
        }
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        recyclerView?.validateNoDataView(llNoData)

        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.frame)
                ?: return@addOnBackStackChangedListener
            if (titleStack.isEmpty()) return@addOnBackStackChangedListener

            setTitle(
                fragment,
                titleStack.peek()
            )
        }

        showBackButtons(false)

    }

    private fun onNavigationItemClick(position: Int, title: String?) {
        when (position) {
            AppConstants.NavigationItem.HOME ->
                loadFragment(fragment = FactsCheckListFragment.newInstance(), title = title)
            AppConstants.NavigationItem.ABOUT_US,
            AppConstants.NavigationItem.PRIVACY_POLICY,
            AppConstants.NavigationItem.HELP_SUPPORT ->
                loadFragment(fragment = WebViewFragment.newInstance())
            else ->
                ToastUtils.makeToast(this, getString(R.string.err_work_is_under_process))
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
        if (currentTab == fragment::class.java.simpleName) {
            setTitle(fragment, title)
            return
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.PermissionsCode.ACTION_MANAGE_OVERLAY -> OnDrawPermissionsUtils.onActivityResult(
                mContext = this
            )
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * To be semantically or contextually correct, maybe change the name
     * and signature of this function to something like:
     *
     * private void showBackButton(boolean show)
     * Just a suggestion.
     */
    fun showBackButtons(isShow: Boolean) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (isShow) {
            //You may not want to open the drawer on swipe from the left in this case  
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            // Remove hamburger
            actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
            // Show back button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                actionBarDrawerToggle?.toolbarNavigationClickListener =
                    View.OnClickListener { // Doesn't have to be onBackPressed
                        onBackPressed()
                    }
                mToolBarNavigationListenerIsRegistered = true
            }
        } else {
            //You must regain the power of swipe for the drawer. 
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

            // Remove back button
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            // Show hamburger 
            actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
            // Remove the/any drawer toggle listener
            actionBarDrawerToggle?.toolbarNavigationClickListener = null
            mToolBarNavigationListenerIsRegistered = false
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }
}