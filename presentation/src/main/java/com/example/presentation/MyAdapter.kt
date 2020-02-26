package com.example.presentation

import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                textSize = 100f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
                setBackgroundColor(Color.GREEN)
            }
        })
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        (holder.itemView as TextView).text = itemList[position % itemList.size]
        holder.itemView.setBackgroundColor(tt[position % itemList.size])
    }

    val itemList = LinkedList<String>().apply {
        add("aaa")
        add("bbb")
        add("ccc")
        add("ddd")
        add("eee")
    }

    val tt = LinkedList<Int>().apply {
        add(Color.BLUE)
        add(Color.GREEN)
        add(Color.GRAY)
        add(Color.CYAN)
        add(Color.YELLOW)
    }
    override fun getItemCount(): Int {
        return Integer.MAX_VALUE
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}