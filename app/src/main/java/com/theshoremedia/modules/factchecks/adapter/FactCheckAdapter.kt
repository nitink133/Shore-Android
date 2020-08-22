package com.theshoremedia.modules.factchecks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.theshoremedia.R
import com.theshoremedia.databinding.RowFactChecksBinding
import com.theshoremedia.modules.factchecks.model.NewsModel
import com.theshoremedia.utils.extensions.verticleText

class FactCheckAdapter(
    private val context: Context,
    var items: List<NewsModel>
) : RecyclerView.Adapter<FactCheckAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactCheckAdapter.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_fact_checks,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]
        holder.bind(model)

    }

    inner class ViewHolder(private val binding: RowFactChecksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: NewsModel) {
            //TODO

            binding.tvNewsSource.verticleText()
        }
    }

}