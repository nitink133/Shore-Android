package com.theshoremedia.modules.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.theshoremedia.R
import com.theshoremedia.database.entity.NewsSourceModel
import com.theshoremedia.database.helper.CustomSourcesDatabaseHelper
import com.theshoremedia.databinding.FragmentFactCheckingSourcesBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.home.adapter.NewsSourceAdapter
import com.theshoremedia.utils.ChromeTabUtils
import com.theshoremedia.utils.DialogUtils
import com.theshoremedia.utils.ObjectUtils
import com.theshoremedia.utils.ToastUtils
import com.theshoremedia.utils.extensions.makeVisible


/**
 * A simple [Fragment] subclass.
 */
class FactCheckingSourcesFragment : BaseFragment() {
    private lateinit var binding: FragmentFactCheckingSourcesBinding
    private lateinit var mCustomSourceAdapter: NewsSourceAdapter
    private lateinit var mShoreSourceAdapter: NewsSourceAdapter
    private var unAddedCustomSourcesList: List<NewsSourceModel> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_fact_checking_sources,
                container,
                false
            )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initCustomSources()
        initShoreSources()
        initListeners()


    }

    private fun initListeners() {
        binding.tvAddCustomSource.setOnClickListener {
            DialogUtils.showSelectionDialog(
                context = mContext,
                title = getString(R.string.news_source),
                items = unAddedCustomSourcesList
            ) {
                it.isSelected = true
                CustomSourcesDatabaseHelper.instance?.update(it)
                ToastUtils.makeToast(
                    mContext,
                    getString(
                        R.string.msg_source_successfully_added_as_verification_source,
                        it.name
                    )
                )
            }
        }
    }


    private fun initCustomSources() {
        val lmCustomSources = LinearLayoutManager(context)
        mCustomSourceAdapter = NewsSourceAdapter(
            isSwipeable = true,
            items = arrayListOf(),
            callBacks = mAdapterCallbacks
        )
        binding.rvCustomSources.layoutManager = lmCustomSources
        binding.rvCustomSources.adapter = mCustomSourceAdapter
        binding.rvCustomSources.isNestedScrollingEnabled = false

        CustomSourcesDatabaseHelper.instance?.getAllSources(this) {
            unAddedCustomSourcesList = it.filter { !it.isSelected }
            val customSourcesList = it.filter { it.isSelected }
            binding.tvCustomSource.makeVisible(isVisible = customSourcesList.isNotEmpty())
            binding.rvCustomSources.makeVisible(isVisible = customSourcesList.isNotEmpty())
            mCustomSourceAdapter.addAll(customSourcesList as ArrayList<NewsSourceModel>)
        }


    }

    private fun initShoreSources() {
        val shoreSourcesList =
            ObjectUtils.readFromAssets(
                mContext!!,
                "shore_sources.json",
                NewsSourceModel::class.java
            )
        val lmShoreSources = LinearLayoutManager(context)
        mShoreSourceAdapter =
            NewsSourceAdapter(
                items = shoreSourcesList as ArrayList<NewsSourceModel>,
                callBacks = mAdapterCallbacks
            )
        binding.rvShoreSources.layoutManager = lmShoreSources
        binding.rvShoreSources.adapter = mShoreSourceAdapter
        binding.rvShoreSources.isNestedScrollingEnabled = false
    }


    private var mAdapterCallbacks: ((view: View) -> Unit) = lit@{

        val actionView = it.getTag(R.string.key_view_name)
        if (it.tag as? NewsSourceModel == null) return@lit
        val model = it.tag as NewsSourceModel

        when (actionView) {
            "ivLaunch" -> {
                ChromeTabUtils.openLinkInChromeTab(model.website, mContext)
            }

            "delete" -> {
                it.isSelected = false
                CustomSourcesDatabaseHelper.instance?.update(model)
            }
            else -> {
                val direction =
                    HomeFragmentDirections.actionToAboutSource(model)
                val mainNavView = requireActivity().findViewById<View>(R.id.frame)
                Navigation.findNavController(mainNavView).navigate(direction)
            }
        }
    }


}
