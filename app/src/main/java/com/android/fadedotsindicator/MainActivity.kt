package com.android.fadedotsindicator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.fadedotsindicator.adapter.MainViewPage2Adapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewPage2Adapter by lazy {
        MainViewPage2Adapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = viewPage2Adapter
        dots_indicator.setViewPager2(view_pager)

        setData()
    }

    private fun setData() {
        val list = mutableListOf<String>()
        list.add("this is first pager")
        list.add("this is second pager")
        list.add("this is third pager")
        list.add("this is fourth pager")
        list.add("this is fifth pager")
        viewPage2Adapter.setData(list)
    }


}