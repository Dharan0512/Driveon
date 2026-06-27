package com.revon.driveon

import android.os.Bundle
import android.widget.ImageView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ImagePreviewActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnClose: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        imageView = findViewById(R.id.imageView)
        btnClose = findViewById(R.id.btnClose)

        val imageUrl = intent.getStringExtra("image_url")

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
        }

        btnClose.setOnClickListener {
            finish()
        }
    }
}