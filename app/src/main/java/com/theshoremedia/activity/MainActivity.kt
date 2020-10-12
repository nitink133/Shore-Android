package com.theshoremedia.activity

import android.animation.AnimatorInflater
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.theshoremedia.R
import com.theshoremedia.database.helper.SourcesDatabaseHelper
import com.theshoremedia.databinding.ActivityMainBinding
import com.theshoremedia.modules.base.BaseActivity
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.navigation.adapter.NavigationDrawerAdapter
import com.theshoremedia.modules.navigation.model.NavigationDataModel
import com.theshoremedia.utils.*
import com.theshoremedia.utils.extensions.makeVisible
import com.theshoremedia.utils.extensions.validateNoDataView
import com.theshoremedia.utils.permissions.AccessibilityPermissionsUtils
import com.theshoremedia.utils.permissions.BatteryOptimizationPermissionsUtils
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils
import com.theshoremedia.utils.permissions.StoragePermissionsUtils
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.layout_navigation_view.*
import kotlinx.android.synthetic.main.layout_recycler_view.*


class MainActivity : BaseActivity(), AppBarConfiguration.OnNavigateUpListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityMainBinding
    var doubleBackToExitPressedOnce = false

    private var appUpdateManager: AppUpdateManager? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        OnDrawPermissionsUtils.checkPermission(mContext = this) {
            StoragePermissionsUtils.checkPermission(mContext = this) {
                AccessibilityPermissionsUtils.verifyPermission(mContext = this) {
                    PreferenceUtils.savePref(getString(R.string.key_auto_detect), it)
                }
            }

//        FactCheckHistoryDatabaseHelper.instance?.addDummyData(this)
            SourcesDatabaseHelper.instance?.storeCustomSourcesInDB(this)
            // initializing navigation menu
            setUpNavigationView()
            setupNavigationController()
            checkForAppUpdate()
        }
    }

    private fun setupNavigationController() {
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.frame) as NavHostFragment? ?: return
        val navController = navHostFragment.navController
        appBarConfiguration =
            AppBarConfiguration(navController.graph) //configure nav controller

        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_past_checks -> setTitle(getString(R.string.past_checks))
                R.id.nav_favourite -> setTitle(getString(R.string.favourites))
                R.id.nav_settings -> setTitle(getString(R.string.settings))
                R.id.nav_home -> setTitle(isElevation = false)
                R.id.nav_search_fragment -> setTitle(getString(R.string.search_result))
                else -> setTitle()
            }
        }


        //fragments load from here but how ?
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_past_checks, R.id.nav_favourite, R.id.nav_home),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
    }


    private fun setUpNavigationView() {
        ivCloseDrawer?.setOnClickListener {
            Handler().postDelayed({
                binding.drawerLayout.closeDrawers()
            }, 250)
        }

        val navigationItems = getNavigationItems()
        val adapter = NavigationDrawerAdapter(items = navigationItems) { position, title ->
            onNavigationItemClick(position, title)

        }
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        recyclerView?.validateNoDataView(llNoData)
    }

    private fun onNavigationItemClick(position: Int, title: String? = null) {
        val navController: NavController = findNavController(R.id.frame)
        Handler().postDelayed({
            binding.drawerLayout.closeDrawers()
        }, 250)
        KeyBoardManager.hideKeyboard(this)

        when (position) {
            AppConstants.NavigationItem.HOME -> {
                navController.navigate(R.id.nav_home)
            }
            AppConstants.NavigationItem.PAST_CHECKS -> {
                navController.navigate(R.id.nav_past_checks)
            }
            AppConstants.NavigationItem.FAVOURITE -> {
                navController.navigate(R.id.nav_favourite)
            }

            AppConstants.NavigationItem.SETTINGS -> {
                navController.navigate(R.id.nav_settings)
            }
            AppConstants.NavigationItem.RATE_US -> {
                DialogUtils.showRatingDialog(this)
            }

            AppConstants.NavigationItem.SHARE_APP -> {
                ApplicationUtils.shareApp(this)
            }
            AppConstants.NavigationItem.ABOUT_US,
            AppConstants.NavigationItem.PRIVACY_POLICY,
            AppConstants.NavigationItem.HELP_SUPPORT -> {
                val type = bundleOf(Pair("type", position))
                navController.navigate(R.id.nav_webview, type)
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
            Handler().postDelayed({
                binding.drawerLayout.closeDrawers()
            }, 250)
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

            AppConstants.PermissionsCode.ACTION_UPDATE_APP -> {
                if (resultCode != RESULT_OK) { //RESULT_OK / RESULT_CANCELED / RESULT_IN_APP_UPDATE_FAILED
                    Log.d(message = "Update flow failed! Result code: $resultCode")
                    // If the update is cancelled or fails,
                    // you can request to start the update again.
                    unregisterInstallStateUpdListener()
                }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.PermissionsCode.ACTION_STORAGE -> StoragePermissionsUtils.onActivityResult(
                mContext = this
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (appBarConfiguration.topLevelDestinations.contains(navHostFragment.navController.currentDestination?.id)) {
                Handler().postDelayed({
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }, 250)
                return true
            } else {
                navHostFragment.navController.navigateUp()
                return true
            }
        }
        return item.onNavDestinationSelected(findNavController(R.id.frame))
                || super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }


    private fun checkForAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager!!.appUpdateInfo

        // Create a listener to track request state updates.
        installStateUpdatedListener =
            InstallStateUpdatedListener { installState ->
                // Show module progress, log state, or install the update.
                if (installState.installStatus() == InstallStatus.DOWNLOADED) // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                    popupSnackbarForCompleteUpdateAndUnregister()
            }

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Request the update.
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                    // Before starting an update, register a listener for updates.
                    appUpdateManager!!.registerListener(installStateUpdatedListener)
                    // Start an update.
                    startAppUpdate(appUpdateInfo, type = AppUpdateType.FLEXIBLE)
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Start an update.
                    startAppUpdate(appUpdateInfo)
                }
            }
        }
    }


    private fun startAppUpdate(
        appUpdateInfo: AppUpdateInfo,
        type: Int = AppUpdateType.IMMEDIATE
    ) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                type,  // The current activity making the update request.
                this,  // Include a request code to later monitor this update request.
                AppConstants.PermissionsCode.ACTION_UPDATE_APP
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
            if (type == AppUpdateType.FLEXIBLE)
                unregisterInstallStateUpdListener()
        }
    }


    /**
     * Displays the snackbar notification and call to action.
     * Needed only for Flexible app update
     */
    private fun popupSnackbarForCompleteUpdateAndUnregister() {
        val snackbar: Snackbar = Snackbar.make(
            binding.drawerLayout,
            getString(R.string.update_downloaded),
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(R.string.restart) {
            appUpdateManager!!.completeUpdate()
        }
        snackbar.setActionTextColor(resources.getColor(R.color.colorAccent))
        snackbar.show()
        unregisterInstallStateUpdListener()
    }

    /**
     * Checks that the update is not stalled during 'onResume()'.
     * However, you should execute this check at all app entry points.
     */
    private fun checkNewAppVersionState() {
        appUpdateManager!!
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                //FLEXIBLE:
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdateAndUnregister()
                }

                //IMMEDIATE:
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    startAppUpdate(appUpdateInfo)
                }
            }
    }

    /**
     * Needed only for FLEXIBLE update
     */
    private fun unregisterInstallStateUpdListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null) appUpdateManager!!.unregisterListener(
            installStateUpdatedListener
        )
    }

    override fun onDestroy() {
        unregisterInstallStateUpdListener()
        super.onDestroy()
    }


    override fun onResume() {
        super.onResume()
        checkNewAppVersionState()
    }
}