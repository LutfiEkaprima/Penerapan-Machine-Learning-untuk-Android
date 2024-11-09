package com.dicoding.asclepius.view.history_view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.database.entity.AnalysisResult
import com.dicoding.asclepius.databinding.ItemHistoryBinding

class HistoryAdapter(private val onClick: (AnalysisResult) -> Unit) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var items = listOf<AnalysisResult>()

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: AnalysisResult) {
            binding.resultLabel.text = item.resultLabel
            binding.resultScore.text = "Confidence: ${String.format("%.2f", item.confidenceScore * 100)}%"

            Glide.with(binding.resultImage.context)
                .load(item.imageUri)
                .into(binding.resultImage)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<AnalysisResult>) {
        items = newItems
        notifyDataSetChanged()
    }
}
