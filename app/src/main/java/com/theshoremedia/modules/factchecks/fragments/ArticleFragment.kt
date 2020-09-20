package com.theshoremedia.modules.factchecks.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.database.helper.FactCheckHistoryDatabaseHelper
import com.theshoremedia.databinding.FragmentArticleViewBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.factchecks.fragments.ArticleFragmentArgs.fromBundle
import com.theshoremedia.utils.ShareUtils
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
        binding.ivNewsIcon.loadImage(factCheckDataModel.image)

        initListener()
    }

    private fun initListener() {

        binding.tvMoreForwardedMsg.setOnClickListener {
            val isExpended: Boolean = binding.tvMoreForwardedMsg.tag as? Boolean ?: false
            binding.tvMoreForwardedMsg.tag = !isExpended
            if (!isExpended) {
                binding.tvMoreForwardedMsg.text = getString(R.string.lbl_less)
                binding.tvForwardedMessage.maxLines = Integer.MAX_VALUE
            } else {
                binding.tvMoreForwardedMsg.text = getString(R.string.lbl_more)
                binding.tvForwardedMessage.maxLines = 4
            }
        }
        binding.tvAboutSourceMore.setOnClickListener {
            val isExpended: Boolean = binding.tvAboutSourceMore.tag as? Boolean ?: false
            binding.tvAboutSourceMore.tag = !isExpended
            if (!isExpended) {
                binding.tvAboutSourceMore.text = getString(R.string.lbl_less)
                binding.tvAboutSourceMore.maxLines = Integer.MAX_VALUE
            } else {
                binding.tvAboutSourceMore.text = getString(R.string.lbl_more)
                binding.tvAboutSourceMore.maxLines = 4
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_article, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.menu_favorite)
        if (factCheckDataModel.isFavourite) {
            item.title = "Remove from favourite"
        } else {
            item.title = "Mark as favourite"
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> requireActivity().onBackPressed()
            R.id.menu_delete -> {
                // Not implemented here
                ToastUtils.makeToast(mContext, getString(R.string.article_delete_successfully))
                FactCheckHistoryDatabaseHelper.instance?.delete(factCheckDataModel)
                requireActivity().onBackPressed()
                return true
            }
            R.id.menu_share -> {
                // Not implemented here
                val isForwardedMsgExpended = binding.tvMoreForwardedMsg.tag as? Boolean ?: false
                if (!isForwardedMsgExpended) binding.tvMoreForwardedMsg.performClick()

                val isAboutSourceMsgExpended = binding.tvAboutSourceMore.tag as? Boolean ?: false
                if (!isAboutSourceMsgExpended) binding.tvAboutSourceMore.performClick()

                ShareUtils.takeScreenshotAndShare(mContext, binding.llRoot)
                return true
            }

            R.id.menu_favorite -> {
                factCheckDataModel.isFavourite = !factCheckDataModel.isFavourite
                FactCheckHistoryDatabaseHelper.instance?.markAsFav(factCheckDataModel)
                ToastUtils.makeToast(
                    mContext, getString(
                        if (factCheckDataModel.isFavourite) R.string.article_marked_as_favourite_successfully else R.string.article_removed_from_favourite_successfully
                    )
                )
                requireActivity().invalidateOptionsMenu()
            }
        }
        return false
    }

    override fun onPageRefreshListener(data: Bundle?) {
        super.onPageRefreshListener(data)
        (mContext as MainActivity).setTitle()
    }
}
