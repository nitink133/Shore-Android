package com.theshoremedia.modules.factchecks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.database.helper.FactCheckHistoryDatabaseHelper
import com.theshoremedia.databinding.FragmentFactsCheckListBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.factchecks.adapter.FactCheckAdapter
import com.theshoremedia.utils.extensions.setFullAnimation
import com.theshoremedia.utils.extensions.setZeroAnimation
import com.theshoremedia.utils.extensions.validateNoDataView
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils


class FavouriteArticlesListFragment : BaseFragment() {
    private lateinit var binding: FragmentFactsCheckListBinding
    private var mAdapter: FactCheckAdapter? = null
    private var isBookMarkStatusChanged: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_facts_check_list, container, false)
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
                        isBookMarkStatusChanged = true
                        val model = it.tag as FactCheckHistoryModel
                        if (model.isFavourite) {
                            (it as LottieAnimationView).setFullAnimation()
                        } else (it as LottieAnimationView).setZeroAnimation()

                        model.isFavourite = !model.isFavourite

                        FactCheckHistoryDatabaseHelper.instance!!.markAsFav(model)

                    }
                    else -> {
                        val direction =
                            FavouriteArticlesListFragmentDirections.actionToFavArticle(it.tag as FactCheckHistoryModel)
                        val mainNavView = requireActivity().findViewById<View>(R.id.frame)
                        Navigation.findNavController(mainNavView).navigate(direction)
                    }
                }
            }
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = mAdapter

        FactCheckHistoryDatabaseHelper.instance?.getFavouriteNews(this) {
            if (isBookMarkStatusChanged) {
                isBookMarkStatusChanged = false
                return@getFavouriteNews
            }
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
        fun newInstance(isFavouriteListRequest: Boolean = false) =
            FavouriteArticlesListFragment()
    }


    override fun onPageRefreshListener(data: Bundle?) {
        super.onPageRefreshListener(data)
        (mContext as MainActivity).setTitle(getString(R.string.bookmark))
    }

}
