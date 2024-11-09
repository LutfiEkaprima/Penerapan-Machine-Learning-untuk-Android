package com.dicoding.asclepius.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_results")
data class AnalysisResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String,
    val resultLabel: String,
    val confidenceScore: Float
)
