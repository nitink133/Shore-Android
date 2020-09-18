package com.theshoremedia.modules.factchecks.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.theshoremedia.R
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.databinding.RowFactCheckBinding
import com.theshoremedia.utils.extensions.loadImage
import com.theshoremedia.utils.extensions.setFull
import com.theshoremedia.utils.extensions.setZero

class FactCheckAdapter(
    var items: ArrayList<FactCheckHistoryModel> = arrayListOf(),
    private var callBacks: (view: View) -> Unit
) : RecyclerView.Adapter<FactCheckAdapter.ViewHolder>() {
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
                R.layout.row_fact_check,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]
        holder.bind(model)

    }

    inner class ViewHolder(private val binding: RowFactCheckBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: FactCheckHistoryModel) {
            binding.model = model
            binding.root.tag = model
            binding.ivBookMark.tag = model
            binding.tvForwardedMessage.isSelected = true
            binding.root.setOnClickListener {
                callBacks.invoke(it)
            }
            if (model.isFavourite) {
                binding.ivBookMark.setFull()
            } else binding.ivBookMark.setZero()


            binding.ivBookMark.setOnClickListener {
                callBacks.invoke(it)
            }


            binding.ivNewsIcon.loadImage(model.image)
        }
    }

}