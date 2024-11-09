package com.dicoding.asclepius.view.history_view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.database.room.AnalysisResultDatabase
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.view.ResultActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private lateinit var analysisResultDatabase: AnalysisResultDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        analysisResultDatabase = AnalysisResultDatabase.getDatabase(this)
        adapter = HistoryAdapter { result ->
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("IMAGE_URI", result.imageUri)
                putExtra("RESULTS", result.resultLabel)
                putExtra("SCORE", result.confidenceScore)
                putExtra("FROM_HISTORY", true)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        loadHistory()
    }

    private fun loadHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            val results = analysisResultDatabase.analisysdao().getAllResults()
            runOnUiThread {
                adapter.submitList(results)
            }
        }
    }
}
