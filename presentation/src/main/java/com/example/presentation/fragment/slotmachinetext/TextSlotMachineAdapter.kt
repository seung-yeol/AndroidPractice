package com.example.presentation.fragment.slotmachinetext

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.presentation.R
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.RollingTextView
import com.yy.mobile.rollingtextview.strategy.Strategy

class TextSlotMachineAdapter : RecyclerView.Adapter<TextSlotMachineAdapter.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    private val items = mutableListOf<CharSequence>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (ViewType.values()[viewType]) {
            ViewType.NUMBER -> {
                LayoutInflater.from(parent.context).inflate(R.layout.item_slot_machine, parent, false).let {
                    ViewHolder.NumberViewHolder(it)
                }
            }
            ViewType.COMMA -> {
                TextView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setTextColor(Color.parseColor("#fffb8c"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.toFloat())
                    typeface = Typeface.DEFAULT_BOLD
                    text = ","
                }.let {
                    ViewHolder.CommaViewHolder(it)
                }
            }
        }

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        when (viewHolder) {
            is ViewHolder.NumberViewHolder -> {
                viewHolder.rollingTextView
            }
            is ViewHolder.CommaViewHolder -> {
                viewHolder.itemView
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] == ",") ViewType.COMMA.ordinal else ViewType.NUMBER.ordinal
    }

    fun setPrice(price: Int) {
        items.clear()
        items.addAll(price.toWon())
        notifyDataSetChanged()
    }

    private fun Int.toWon(): List<CharSequence> = toString().reversed().chunked(3).flatMap {
        listOf(",", it)
    }

    enum class ViewType {
        NUMBER, COMMA
    }

    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class NumberViewHolder(itemView: View) : ViewHolder(itemView) {
            val rollingTextView: RollingTextView = itemView.findViewById<RollingTextView>(R.id.rollingTextView).apply {
                animationDuration = 3000L
                charStrategy = Strategy.CarryBitAnimation()
                addCharOrder(CharOrder.Number)
                animationInterpolator = DecelerateInterpolator()
                setText("0")
                setText("000")
            }

            init {
            }
        }

        class CommaViewHolder(itemView: View) : ViewHolder(itemView)
    }
}
