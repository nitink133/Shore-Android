package com.theshoremedia.modules.factchecks.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.databinding.FragmentArticleViewBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.factchecks.fragments.ArticleFragmentArgs.fromBundle
import com.theshoremedia.utils.ToastUtils
import com.theshoremedia.utils.extensions.loadImage


class ArticleFragment : BaseFragment() {

    private lateinit var binding: FragmentArticleViewBinding
    private val factCheckDataModel: FactCheckHistoryModel by lazy {
        fromBundle(arguments!!).articleModel
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_article_view, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.model = factCheckDataModel
        binding.ivNewsIcon.loadImage(factCheckDataModel.icon)
    }

    companion object {

        @JvmStatic
        fun newInstance(factCheckDataModel: FactCheckHistoryModel) =
            ArticleFragment()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_article, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> requireActivity().onBackPressed()
            R.id.nav_delete -> {
                // Not implemented here
                ToastUtils.makeToast(mContext, getString(R.string.err_work_is_under_process))
                return true
            }
            R.id.nav_share -> {
                // Not implemented here
                ToastUtils.makeToast(mContext, getString(R.string.err_work_is_under_process))
                return true
            }
        }
        return false
    }

    override fun onPageRefreshListener(data: Bundle?) {
        super.onPageRefreshListener(data)
        (mContext as MainActivity).setTitle()
    }
}
