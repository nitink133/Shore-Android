package com.theshoremedia.modules.floatingview.credibility_checker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.theshoremedia.R
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.databinding.RowOverlayFactChecksBinding
import com.theshoremedia.utils.extensions.loadImage
import com.theshoremedia.utils.extensions.setFull
import com.theshoremedia.utils.extensions.setZero
import com.theshoremedia.utils.extensions.verticleText

class OverlayFactCheckAdapter(
    var items: ArrayList<FactCheckHistoryModel>,
    private var callBacks: (view: View) -> Unit
) : RecyclerView.Adapter<OverlayFactCheckAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    fun addAll(items: ArrayList<FactCheckHistoryModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_overlay_fact_checks,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]
        holder.bind(model)

    }

    inner class ViewHolder(private val binding: RowOverlayFactChecksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: FactCheckHistoryModel) {
            //TODO
            binding.model = model
            binding.root.setTag(R.string.key_model, model)
            binding.root.setTag(R.string.key_view_name, "root")
            binding.ivMarkFav.setTag(R.string.key_model, model)
            binding.ivMarkFav.setTag(R.string.key_view_name, "ivMarkFav")
            if (model.isFavourite) {
                binding.ivMarkFav.setFull()
            } else binding.ivMarkFav.setZero()

            binding.root.setOnClickListener {
                callBacks.invoke(it)
            }

            binding.ivMarkFav.setOnClickListener {
                if (model.isFavourite)
                    binding.ivMarkFav.setZero()
                else binding.ivMarkFav.setFull()
                callBacks.invoke(it)
            }

            binding.tvNewsSource.verticleText(model.sourceName)

            binding.ivNewsIcon.loadImage(model.image)
        }
    }

}