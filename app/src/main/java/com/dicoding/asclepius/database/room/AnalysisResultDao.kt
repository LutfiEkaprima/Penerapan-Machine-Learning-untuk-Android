package com.dicoding.asclepius.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dicoding.asclepius.database.entity.AnalysisResult

@Dao
interface AnalysisResultDao {
    @Insert
    suspend fun insertResult(result: AnalysisResult)

    @Query("SELECT * FROM analysis_results")
    suspend fun getAllResults(): List<AnalysisResult>
}