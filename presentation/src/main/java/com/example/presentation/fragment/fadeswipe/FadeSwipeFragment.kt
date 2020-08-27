package com.example.presentation.fragment.fadeswipe

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.presentation.R
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.item.view.*
import java.util.*


class FadeSwipeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initView()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerView.adapter =
            TestAdapter()
        recyclerView.setChildDrawingOrderCallback { childCount, i ->
            childCount - i - 1
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visiblePosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                val temp = Rect()

                recyclerView.children.forEach {
                    val viewHolder = recyclerView.findContainingViewHolder(it)
                    if (viewHolder != null) {
                        when (viewHolder.layoutPosition) {
                            visiblePosition -> {
                                it.translationX = -it.left.toFloat()
                                it.alpha = it.right.toFloat() / it.width
                            }
                            visiblePosition + 1 -> {
                                it.translationX = -it.left.toFloat()
                                it.alpha = 1f
                            }
                        }
                    }
                }
            }
        })
    }

    private fun initView() {
        preBtn.setOnClickListener {
            var currentPage =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (currentPage > 0) {
                currentPage--

                val sss = SlowSmoothScroll(
                    requireContext()
                ).apply { targetPosition = currentPage }
                (recyclerView.layoutManager as LinearLayoutManager).startSmoothScroll(sss)
            }
        }

        nextBtn.setOnClickListener {
            var currentPage =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (currentPage < recyclerView.adapter!!.itemCount - 1) {
                currentPage++
                recyclerView.smoothScrollToPosition(currentPage)

                val sss = SlowSmoothScroll(
                    requireContext()
                ).apply {
                    targetPosition = currentPage
                }
                (recyclerView.layoutManager as LinearLayoutManager).startSmoothScroll(sss)
            }
        }
    }

    class SlowSmoothScroll(context: Context) : LinearSmoothScroller(context) {
        override fun calculateTimeForScrolling(dx: Int): Int {
            return 500
        }
    }

    class TestAdapter : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {
        private val items = LinkedList<Pair<Int, String>>()

        init {
            items.add(Color.RED to "RED")
            items.add(Color.YELLOW to "YELLOW")
            items.add(Color.GREEN to "GREEN")
            items.add(Color.BLUE to "BLUE")
            items.add(Color.CYAN to "CYAN")
            items.add(Color.MAGENTA to "MAGENTA")
            items.add(Color.GRAY to "GRAY")
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
            return TestViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount() = items.size

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
            val view = holder.itemView
            val item = items[position]

            view.setBackgroundColor(item.first)
            holder.textView.text = item.second

            holder.itemView.setOnClickListener {
                println("OSY position : $position")
            }
        }

        class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.text
        }
    }
}