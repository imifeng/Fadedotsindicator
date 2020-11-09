package com.android.fadedotsindicator.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.fadedotsindicator.R
import com.android.fadedotsindicator.inflate

class MainViewPage2Adapter : RecyclerView.Adapter<MainViewPage2ViewHolder>() {


    private val list = mutableListOf<String>()

    fun setData(data: List<String>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewPage2ViewHolder {
        return MainViewPage2ViewHolder(parent.inflate(R.layout.rv_pager_view))
    }

    override fun onBindViewHolder(holder: MainViewPage2ViewHolder, position: Int) {
        holder.bind(position, list[position])
    }

}