package com.theshoremedia.activity

import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.theshoremedia.R
import com.theshoremedia.database.helper.CustomSourcesDatabaseHelper
import com.theshoremedia.database.helper.FactCheckHistoryDatabaseHelper
import com.theshoremedia.databinding.ActivityMainBinding
import com.theshoremedia.modules.base.BaseActivity
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.navigation.adapter.NavigationDrawerAdapter
import com.theshoremedia.modules.navigation.model.NavigationDataModel
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.KeyBoardManager
import com.theshoremedia.utils.PreferenceUtils
import com.theshoremedia.utils.ToastUtils
import com.theshoremedia.utils.extensions.makeVisible
import com.theshoremedia.utils.extensions.validateNoDataView
import com.theshoremedia.utils.permissions.AccessibilityPermissionsUtils
import com.theshoremedia.utils.permissions.BatteryOptimizationPermissionsUtils
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.layout_navigation_view.*
import kotlinx.android.synthetic.main.layout_recycler_view.*


class MainActivity : BaseActivity(), AppBarConfiguration.OnNavigateUpListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityMainBinding
    var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        OnDrawPermissionsUtils.checkPermission(mContext = this) {
            AccessibilityPermissionsUtils.verifyPermission(mContext = this) {
                PreferenceUtils.savePref(getString(R.string.key_auto_detect), it)
            }
        }

        FactCheckHistoryDatabaseHelper.instance?.addDummyData(this)
        CustomSourcesDatabaseHelper.instance?.storeCustomSourcesInDB(this)
        // initializing navigation menu
        setUpNavigationView()
        setupNavigationController()
    }

    private fun setupNavigationController() {
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.frame) as NavHostFragment? ?: return
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph) //configure nav controller

        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_past_checks -> setTitle(getString(R.string.past_checks))
                R.id.nav_favorite -> setTitle(getString(R.string.favourites))
                R.id.nav_settings -> setTitle(getString(R.string.settings))
                R.id.nav_home -> setTitle(isElevation = false)
                else -> setTitle()
            }
        }


        //fragments load from here but how ?
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_past_checks, R.id.nav_favorite, R.id.nav_home),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }


    private fun setUpNavigationView() {
        ivCloseDrawer?.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        val navigationItems = getNavigationItems()
        val adapter = NavigationDrawerAdapter(items = navigationItems) { position, title ->
            Handler().postDelayed({
                onNavigationItemClick(position, title)
            }, 300)

        }
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        recyclerView?.validateNoDataView(llNoData)
    }

    private fun onNavigationItemClick(position: Int, title: String? = null) {
        val navController: NavController = findNavController(R.id.frame)
        binding.drawerLayout.closeDrawers()
        KeyBoardManager.hideKeyboard(this)

        when (position) {
            AppConstants.NavigationItem.HOME -> {
                navController.navigate(R.id.nav_home)
            }
            AppConstants.NavigationItem.PAST_CHECKS -> {
                navController.navigate(R.id.nav_past_checks)
            }
            AppConstants.NavigationItem.FAVOURITE -> {
                navController.navigate(R.id.nav_favorite)
            }

            AppConstants.NavigationItem.SETTINGS -> {
                navController.navigate(R.id.nav_settings)
            }

            AppConstants.NavigationItem.ABOUT_US,
            AppConstants.NavigationItem.PRIVACY_POLICY,
            AppConstants.NavigationItem.HELP_SUPPORT -> {
                navController.navigate(R.id.nav_webview)
            }
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
        when (navHostFragment.navController.graph.startDestination) {
            navHostFragment.navController.currentDestination?.id -> showExitConfirmation()
            else -> {
                super.onBackPressed()
                val currentFragment = navHostFragment.childFragmentManager.fragments[0]
                if (currentFragment is BaseFragment) currentFragment.onPageRefreshListener()
            }
        }
    }

    private fun showExitConfirmation() {
        if (doubleBackToExitPressedOnce) {
            clearBackStack(this)
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    fun setTitle(title: String? = null, isElevation: Boolean = true) {
        appbarLayout?.stateListAnimator = AnimatorInflater.loadStateListAnimator(
            applicationContext,
            if (isElevation) R.animator.anim_elevation else R.animator.anim_no_elevation
        )
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

            AppConstants.PermissionsCode.ACTION_ACCESSIBILITY -> AccessibilityPermissionsUtils.onActivityResult(
                mContext = this
            )

            AppConstants.PermissionsCode.ACTION_BATTERY_SAVER -> BatteryOptimizationPermissionsUtils.onActivityResult(
                mContext = this
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (appBarConfiguration.topLevelDestinations.contains(navHostFragment.navController.currentDestination?.id)) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        }
        return item.onNavDestinationSelected(findNavController(R.id.frame))
                || super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }


}