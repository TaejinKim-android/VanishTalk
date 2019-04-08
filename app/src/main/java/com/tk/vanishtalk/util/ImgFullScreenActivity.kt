package com.tk.vanishtalk.util

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.tk.vanishtalk.R
import kotlinx.android.synthetic.main.activity_img_full_screen.*

class ImgFullScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_full_screen)

        val imgUri = intent.getStringExtra(getString(R.string.basic_img_key))

        Glide.with(this)
            .load(imgUri)
            .diskCacheStrategy(DiskCacheStrategy.ALL) //use this to cache img
            .transition(withCrossFade())
            .into(img_full_screen_imageView)
    }
}
