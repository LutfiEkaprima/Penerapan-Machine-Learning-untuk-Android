package com.dicoding.asclepius.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.history_view.HistoryActivity
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var currentImageUri: Uri? = null

    private val viewModel: MainViewModel by viewModels()

    companion object {
        private const val REQUEST_CODE_GALLERY = 100
        private const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = this
        )

        viewModel.imageUri?.let {
            currentImageUri = it
            showImage()
        }

        binding.galleryButton.setOnClickListener {
            if (isStoragePermissionRequired()) {
                if (checkStoragePermission()) {
                    startGallery()
                } else {
                    requestStoragePermission()
                }
            } else {
                startGallery()
            }
        }

        binding.analyzeButton.setOnClickListener { analyzeImage() }
    }

    private fun isStoragePermissionRequired(): Boolean {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_READ_EXTERNAL_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGallery()
            } else {
                showToast("Permission required to access gallery.")
            }
        }
    }

    private fun startGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            currentImageUri = data?.data
            viewModel.imageUri = currentImageUri
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        currentImageUri?.let {
            imageClassifierHelper.classifyStaticImage(it)
            binding.progressIndicator.visibility = android.view.View.VISIBLE
        } ?: showToast("Please select an image first.")
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        binding.progressIndicator.visibility = android.view.View.GONE
        results?.let {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("RESULTS", it[0].categories[0].label)
            intent.putExtra("SCORE", it[0].categories[0].score)
            intent.putExtra("IMAGE_URI", currentImageUri.toString())
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.progressIndicator.visibility = android.view.View.GONE
    }

    override fun onError(error: String) {
        binding.progressIndicator.visibility = android.view.View.GONE
        showToast(error)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
