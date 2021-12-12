package com.yml.chatapp.ui.home.preview

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yml.chatapp.R
import com.yml.chatapp.common.SET_IMAGE
import com.yml.chatapp.databinding.ActivityPreviewImageBinding

class PreviewImage : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewImageBinding
    private lateinit var imageString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        intent.extras?.let {
//            imageByteArray = it.getByteArray(SET_IMAGE) ?: ByteArray(0)
//        }
        imageString = intent.getStringExtra(SET_IMAGE).toString()
        imagePreview()
        allListeners()
    }

    private fun allListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.sendButton.setOnClickListener {
            val intent = Intent()
            intent.putExtra(SET_IMAGE, imageString)
            setResult(0, intent)
            finish()
        }
    }

    private fun imagePreview() {
//        val imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
        val imageUri = Uri.parse(imageString)
        binding.setImage.setImageURI(imageUri)
    }
}