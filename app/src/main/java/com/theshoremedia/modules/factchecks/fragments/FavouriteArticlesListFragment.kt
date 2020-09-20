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
import com.theshoremedia.databinding.FragmentPastFactsCheckListBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.factchecks.adapter.FactCheckAdapter
import com.theshoremedia.utils.extensions.validateNoDataView
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils


class FavouriteArticlesListFragment : BaseFragment() {
    private lateinit var binding: FragmentPastFactsCheckListBinding
    private var mAdapter: FactCheckAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_past_facts_check_list,
                container,
                false
            )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        OnDrawPermissionsUtils.checkPermission(mContext!!) {
//            AccessibilityPermissionsUtils.checkPermission(mContext!!) {
//                //TODO
////                BubbleCredibilityCheckerView.getInstance(mContext!!).init()
//            }
        }

        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        val recyclerView =
            binding.llRecyclerView.findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        mAdapter =
            FactCheckAdapter {
                when (it.id) {
                    R.id.ivBookMark -> {
                        val model = it.tag as FactCheckHistoryModel
                        model.isFavourite = !model.isFavourite

                        FactCheckHistoryDatabaseHelper.instance!!.markAsFav(model)

                    }
                    else -> {
                        val direction =
                            FavouriteArticlesListFragmentDirections.actionToFavArticle(it.tag as FactCheckHistoryModel)
                        getNavController().navigate(direction)
                    }
                }
            }
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = mAdapter

        initRecyclerViewData()
    }

    private fun initRecyclerViewData() {
        FactCheckHistoryDatabaseHelper.instance?.getFavouriteNews {
            mAdapter?.addAll(items = it as ArrayList<FactCheckHistoryModel>)
            binding.llRecyclerView.findViewById<RecyclerView>(R.id.recyclerView)
                ?.validateNoDataView(
                    binding.llRecyclerView.findViewById(
                        R.id.llNoData
                    )
                )
        }
    }


    override fun onPageRefreshListener(data: Bundle?) {
        super.onPageRefreshListener(data)
        (mContext as MainActivity).setTitle(getString(R.string.favourites))
        initRecyclerViewData()
    }

}
