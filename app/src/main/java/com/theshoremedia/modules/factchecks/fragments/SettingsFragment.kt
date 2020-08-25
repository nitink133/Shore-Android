package com.theshoremedia.modules.factchecks.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.theshoremedia.R
import com.theshoremedia.utils.permissions.AccessibilityPermissionsUtils
import com.theshoremedia.utils.permissions.BatteryOptimizationPermissionsUtils


/**
 * @author Nitin Khanna
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefernce_settings, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setBackgroundColor(resources.getColor(R.color.colorPrimary))
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            null -> return
            getString(R.string.key_auto_detect) -> {
                if (!(findPreference(key) as SwitchPreference).isChecked) return
                AccessibilityPermissionsUtils.checkPermission(mContext = this.requireContext()) { isEnabled ->
                    (findPreference(key) as SwitchPreference).isChecked = isEnabled
                }
            }
            getString(R.string.key_allow_battery_optimization) -> {
                if (!(findPreference(key) as SwitchPreference).isChecked) return
                BatteryOptimizationPermissionsUtils.checkPermission(mContext = this.requireContext()) { isEnabled ->
                    (findPreference(key) as SwitchPreference).isChecked = isEnabled
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }


}