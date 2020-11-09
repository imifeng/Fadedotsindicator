package com.android.fadedotsindicator.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_pager_view.view.*

class MainViewPage2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(position: Int, item: String) {
        with(itemView) {
            tv_pager.text = "$item - $position"
        }
    }

}