package com.theshoremedia.modules.factchecks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.database.helper.FactCheckHistoryDatabaseHelper
import com.theshoremedia.databinding.FragmentFactssCheckListBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.factchecks.adapter.FactCheckAdapter
import com.theshoremedia.utils.extensions.validateNoDataView
import com.theshoremedia.utils.permissions.AccessibilityPermissionsUtils
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils


class FactsCheckListFragment : BaseFragment() {
    private lateinit var binding: FragmentFactssCheckListBinding
    private var mAdapter: FactCheckAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_factss_check_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        OnDrawPermissionsUtils.checkPermission(mContext!!) {
            AccessibilityPermissionsUtils.checkPermission(mContext!!) {
                //TODO
            }
        }

        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        val recyclerView =
            binding.llRecyclerView.findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        mAdapter =
            FactCheckAdapter(
                mContext!!,
                arrayListOf()
            ) { _, model ->
                (mContext as MainActivity).loadFragment(
                    fragment = ArticleFragment.newInstance(factCheckDataModel = model)
                )
            }
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = mAdapter
        FactCheckHistoryDatabaseHelper.instance?.getAllNews(this) {
            mAdapter?.addAll(items = it as ArrayList<FactCheckHistoryModel>)
            recyclerView?.validateNoDataView(
                binding.llRecyclerView.findViewById(
                    R.id.llNoData
                )
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FactsCheckListFragment()
    }


}
