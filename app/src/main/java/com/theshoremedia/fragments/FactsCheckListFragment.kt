package com.theshoremedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.theshoremedia.R
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.utils.permissions.AccessibilityPermissionsUtils
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils


class FactsCheckListFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_factss_check_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        OnDrawPermissionsUtils.checkPermission(mContext!!) {
            AccessibilityPermissionsUtils.checkPermission(mContext!!) {
//                Toast.makeText(mContext!!, "Thanks", Toast.LENGTH_LONG).show()
            }
//            BubbleCredibilityCheckerView.getInstance(mContext!!).init()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FactsCheckListFragment()
    }


}
