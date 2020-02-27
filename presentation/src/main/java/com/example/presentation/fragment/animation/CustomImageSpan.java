package com.example.presentation.fragment.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class CustomImageSpan extends ImageSpan {

    /**
     * A constant indicating that the center of this span should be aligned
     * with the center of the surrounding text
     */
    public static final int ALIGN_CENTER = -12;
    private WeakReference<Drawable> mDrawable;
    private int mAlignment;

    public CustomImageSpan(Context context, final int drawableRes, int alignment) {
        super(context, drawableRes);
        mAlignment = alignment;
    }

    public CustomImageSpan(final Drawable drawable, int alignment) {
        super(drawable);
        mAlignment = alignment;
    }

    @Override
    public int getSize(Paint paint, CharSequence text,
                       int start, int end,
                       Paint.FontMetricsInt fm) {
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt pfm = paint.getFontMetricsInt();
            fm.ascent = pfm.ascent;
            fm.descent = pfm.descent;
            fm.top = pfm.top;
            fm.bottom = pfm.bottom;
        }
        return rect.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        if (mAlignment == ALIGN_CENTER) {
            Drawable cachedDrawable = getCachedDrawable();
            canvas.save();
            //Get the center point and set the Y coordinate considering the drawable height for aligning the icon vertically
            int transY = ((top + bottom) / 2) - cachedDrawable.getIntrinsicHeight() / 2;
            canvas.translate(x, transY);
            cachedDrawable.draw(canvas);
            canvas.restore();
        } else {
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }
    }

    // Redefined locally because it is a private member from DynamicDrawableSpan
    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawable;
        Drawable d = null;
        if (wr != null) {
            d = wr.get();
        }
        if (d == null) {
            d = getDrawable();
            mDrawable = new WeakReference<>(d);
        }
        return d;
    }
}