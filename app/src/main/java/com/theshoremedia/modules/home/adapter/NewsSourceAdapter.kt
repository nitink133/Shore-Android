package com.theshoremedia.modules.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.theshoremedia.R
import com.theshoremedia.database.entity.NewsSourceModel
import com.theshoremedia.databinding.RowNewsSourceBinding
import com.theshoremedia.databinding.RowNewsSourceSwipeableBinding
import com.theshoremedia.utils.extensions.loadImage

/**
 * @author- Nitin Khanna
 * @date -
 */

class NewsSourceAdapter(
    private var isSwipeable: Boolean = false,
    var items: ArrayList<NewsSourceModel> = arrayListOf(),
    private var callBacks: (view: View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var viewBinderHelper = ViewBinderHelper()
    override fun getItemCount(): Int {
        return items.size
    }

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    fun addAll(items: ArrayList<NewsSourceModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isSwipeable) SwipeableViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_news_source_swipeable,
                parent,
                false
            )
        ) else ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_news_source,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = items[position]

        if (isSwipeable) {
            viewBinderHelper.bind((holder as SwipeableViewHolder).binding.root, "$position")
            holder.bind(model)
        } else {
            (holder as ViewHolder).bind(model)
        }
    }

    inner class ViewHolder(val binding: RowNewsSourceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: NewsSourceModel) {
            binding.model = model
            binding.root.tag = model
            binding.root.setTag(R.string.key_view_name, "root")
            binding.ivLaunch.tag = model
            binding.ivLaunch.setTag(R.string.key_view_name, "ivLaunch")

            binding.root.setOnClickListener {
                callBacks.invoke(it)
            }

            binding.ivLaunch.setOnClickListener {
                callBacks.invoke(it)
            }

            binding.ivSourceIcon.loadImage(model.logo)
        }
    }


    inner class SwipeableViewHolder(val binding: RowNewsSourceSwipeableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: NewsSourceModel) {
            binding.model = model
            binding.llContentView.tag = model
            binding.llContentView.setTag(R.string.key_view_name, "root")
            binding.ivLaunch.tag = model
            binding.ivLaunch.setTag(R.string.key_view_name, "ivLaunch")
            binding.delete.tag = model
            binding.delete.setTag(R.string.key_view_name, "delete")

            binding.llContentView.setOnClickListener {
                callBacks.invoke(it)
            }

            binding.ivLaunch.setOnClickListener {
                callBacks.invoke(it)
            }

            binding.ivSourceIcon.loadImage(model.logo)

            binding.delete.setOnClickListener {
                binding.root.close(true)
                callBacks.invoke(it)
            }
        }
    }

}