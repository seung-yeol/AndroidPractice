package com.example.presentation.fragment.webp

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.UnitTransformation
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.presentation.R
import kotlinx.android.synthetic.main.fragment_webp.*


class WebPFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_webp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webPImage.visibility = View.GONE
        Glide.with(requireContext())
            .load(R.raw.damn2)
            .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(UnitTransformation.get()))
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    (resource as WebpDrawable).loopCount = 1

                    return false
                }
            })
            .into(webPImage)

        Glide.with(requireContext())
            .load(R.raw.damn2)
            .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(UnitTransformation.get()))
            .into(webPImage2)

        animationStartButton.setOnClickListener {
            webPImage.visibility = View.VISIBLE
            webPImage2.visibility = View.VISIBLE
            (webPImage.drawable as WebpDrawable).start()
        }
    }
}