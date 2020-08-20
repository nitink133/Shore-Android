package com.theshoremedia.modules.navigation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.theshoremedia.R
import com.theshoremedia.databinding.RowNavigationBinding
import com.theshoremedia.modules.navigation.model.NavigationDataModel

class NavigationDrawerAdapter(
    private var items: ArrayList<NavigationDataModel> = arrayListOf(),
    private var callBacks: ((position: Int, title: String?) -> Unit)
) :
    RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_navigation,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: RowNavigationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: NavigationDataModel) {
            binding.model = model
            binding.root.setOnClickListener {
                callBacks.invoke(position, model.toolbarTitle)
            }
        }
    }
}