package com.theshoremedia.modules.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.databinding.FragmentHomeBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.base.adapter.BaseViewPagerAdapter


class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewPager()
        binding.tabLayout.setupWithViewPager(binding.viewpager)
    }


    // Add Fragments to Tabs
    private fun setupViewPager() {
        val adapter =
            BaseViewPagerAdapter(
                childFragmentManager
            )
        adapter.addFragment(ManualFactCheckingFragment(), getString(R.string.fact_checks))
        adapter.addFragment(FactCheckingSourcesFragment(), getString(R.string.sources))
        binding.viewpager.adapter = adapter
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onPageRefreshListener(data: Bundle?) {
        super.onPageRefreshListener(data)
        (mContext as MainActivity).setTitle(isElevation = false)
    }
}
