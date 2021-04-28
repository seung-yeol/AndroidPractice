package com.example.presentation.fragment.start

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.presentation.R
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.android.synthetic.main.item.view.*
import java.util.*

class StartFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initView()
    }

    private fun initRecyclerView() {
        에러를 만들어 보자
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.adapter =
            MyAdapter(
                LinkedList<MyAdapter.Item>().apply {
                    add(
                        MyAdapter.Item(
                            Color.RED,
                            "Clip Swipe",
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToClipSwipeFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.parseColor("#FF5E00"),
                            "Fade Swipe",
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToFadeSwipeFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.YELLOW,
                            "Animation Set",
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToAnimationFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.BLUE,
                            "Range Seekbar",
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToRangeSeekBarFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.MAGENTA,
                            "animated Drawable",
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToDrawableFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.RED,
                            "animated Drawable2",
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToDrawableFragment2())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.GREEN,
                            "scratch view",
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToScratchFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.GRAY,
                            "custom layout",
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToCustomLayoutFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.YELLOW,
                            "custom layout2",
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToCustomLayoutFragment2())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.RED,
                            "RX",
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToRxFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.CYAN,
                            "CUSTOM TEXTVIEW"
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToCustomTextViewFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.GREEN,
                            "webpImage Fragment"
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToWebPFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.MAGENTA,
                            "eventDriven Fragment"
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToEventDrivenFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.RED,
                            "screenCapture Fragment"
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToScreenCaptureFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.GREEN,
                            "lottie seekBar Fragment"
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToLottieSeekBarFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.YELLOW,
                            "slot machine Fragment"
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToSlotMachineFragment())
                        }
                    )

                    add(
                        MyAdapter.Item(
                            Color.RED,
                            "visible Fragment"
                        ) {
                            findNavController().navigate(StartFragmentDirections.actionStartFragmentToVisibleFragment())
                        }
                    )
                }
            )
        recyclerView.itemAnimator = MyItemAnimator()
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

        inflate.setOnClickListener {
            stub.visibility = View.VISIBLE
        }
    }

    class SlowSmoothScroll(context: Context) : LinearSmoothScroller(context) {
        override fun calculateTimeForScrolling(dx: Int): Int {
            return 500
        }
    }

    class MyAdapter(private val items: List<Item> = arrayListOf()) :
        RecyclerView.Adapter<MyAdapter.TestViewHolder>() {

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

        override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
            val view = holder.itemView
            val item = items[position]

            view.setBackgroundColor(item.color)
            holder.textView.text = item.text
            holder.itemView.setOnClickListener(item.onClickListener)
        }

        data class Item(val color: Int, val text: String, val onClickListener: View.OnClickListener)

        class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView = itemView.text
        }
    }
}