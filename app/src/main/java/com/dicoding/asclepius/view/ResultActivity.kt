package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.entity.AnalysisResult
import com.dicoding.asclepius.database.room.AnalysisResultDatabase
import com.dicoding.asclepius.databinding.ActivityResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var analysisResultDatabase: AnalysisResultDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        analysisResultDatabase = AnalysisResultDatabase.getDatabase(this)

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

        binding.saveButton.setOnClickListener {
            saveAnalysisResult(imageUriString, classificationResult, classificationScore)
            binding.saveButton.isEnabled = false
            binding.saveButton.text = "Saved"
        }

    }

    private fun saveAnalysisResult(imageUri: String?, resultLabel: String?, confidenceScore: Float) {
        if (imageUri != null && resultLabel != null) {
            val analysisResult = AnalysisResult(
                imageUri = imageUri,
                resultLabel = resultLabel,
                confidenceScore = confidenceScore
            )

            lifecycleScope.launch(Dispatchers.IO) {
                analysisResultDatabase.analisysdao().insertResult(analysisResult)
                runOnUiThread {
                    Toast.makeText(this@ResultActivity, "Hasil analisis berhasil disimpan", Toast.LENGTH_SHORT).show()
                    binding.saveButton.isEnabled = false
                }
            }
        } else {
            Toast.makeText(this, "Data analisis tidak lengkap", Toast.LENGTH_SHORT).show()
        }
    }
}