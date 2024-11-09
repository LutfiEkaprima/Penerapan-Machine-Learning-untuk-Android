package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra("IMAGE_URI")
        val classificationResult = intent.getStringExtra("RESULTS")
        val classificationScore = intent.getFloatExtra("SCORE", 0.0f)


        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            binding.resultImage.setImageURI(imageUri)
        }

        binding.resultText.text = if (classificationResult != null) {
            val scorePercentage = String.format("%.2f", classificationScore * 100)
            "$classificationResult $scorePercentage%"
        } else {
            getString(R.string.image_classifier_failed)
        }


    }
}
