package com.theshoremedia.modules.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

/**
 * @author- Nitin Khanna
 * @date -
 */
abstract class BaseActivity : AppCompatActivity() {


    fun clearBackStack(appCompatActivity: AppCompatActivity) {
        val manager = appCompatActivity.supportFragmentManager
        if (manager.backStackEntryCount > 1) {
            val first = manager.getBackStackEntryAt(1)
            manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }


}