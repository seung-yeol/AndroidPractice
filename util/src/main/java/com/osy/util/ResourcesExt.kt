package com.osy.util

import android.content.res.Resources
import android.util.TypedValue

fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Float.toDp() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.dpToPx() = this.toFloat().dpToPx()

fun Float.dpToPx() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics).toInt()

fun Int.spToPx() = this.toFloat().spToPx()

fun Float.spToPx() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics).toInt()