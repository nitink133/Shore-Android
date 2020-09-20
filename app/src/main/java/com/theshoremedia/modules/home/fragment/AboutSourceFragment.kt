package com.theshoremedia.modules.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.database.entity.NewsSourceModel
import com.theshoremedia.databinding.FragmentAboutSourceBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.home.fragment.AboutSourceFragmentArgs.fromBundle
import com.theshoremedia.utils.ChromeTabUtils
import com.theshoremedia.utils.extensions.loadImage


class AboutSourceFragment : BaseFragment() {
    private lateinit var binding: FragmentAboutSourceBinding
    private val newsSourceModel: NewsSourceModel by lazy {
        fromBundle(arguments!!).newsSourceModel
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_about_source, container, false)

        //set source name as toolbar title
        (mContext as MainActivity).setTitle(newsSourceModel.name)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.model = newsSourceModel
        binding.ivNewsIcon.loadImage(newsSourceModel.logo)
        binding.ivIFCNBadge.loadImage(newsSourceModel.ifcnLogo)

        initListener()
    }

    private fun initListener() {
        binding.tvWebsite.setOnClickListener {
            ChromeTabUtils.openLinkInChromeTab(newsSourceModel.website, mContext)
        }
    }

    override fun onPageRefreshListener(data: Bundle?) {
        super.onPageRefreshListener(data)
        (mContext as MainActivity).setTitle(newsSourceModel.name)
    }
}
