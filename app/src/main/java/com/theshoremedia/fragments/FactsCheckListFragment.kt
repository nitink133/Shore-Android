package com.theshoremedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.utils.permissions.AccessibilityPermissionsUtils
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils
import com.theshoremedia.views.AccessibilityGrantedDemoView


class FactsCheckListFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_factss_check_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        OnDrawPermissionsUtils.checkPermission(mContext!!) {
            AccessibilityPermissionsUtils.checkPermission(mContext!!) {
                Toast.makeText(mContext!!, "Thanks", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FactsCheckListFragment()
    }


}
