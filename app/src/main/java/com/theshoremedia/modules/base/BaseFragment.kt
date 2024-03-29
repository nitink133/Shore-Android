package com.theshoremedia.modules.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.theshoremedia.R

/**
 * @author- Nitin Khanna
 * @date -
 */
open class BaseFragment : Fragment() {
    var mContext: Context? = null
    var TAG: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    open fun onPageRefreshListener(data: Bundle? = null) {

    }

    fun getNavController(): NavController {
        val mainNavView = requireActivity().findViewById<View>(R.id.frame)
        return Navigation.findNavController(mainNavView)
    }

    fun navigate(destination: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(destination.actionId)
            ?.let { navigate(destination) }
    }
}