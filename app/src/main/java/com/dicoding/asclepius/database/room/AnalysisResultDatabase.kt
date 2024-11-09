package com.dicoding.asclepius.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.database.entity.AnalysisResult

@Database(entities = [AnalysisResult::class], version = 1)
abstract class AnalysisResultDatabase : RoomDatabase() {
    abstract fun analisysdao(): AnalysisResultDao

    companion object {
        @Volatile private var INSTANCE: AnalysisResultDatabase? = null

        fun getDatabase(context: Context): AnalysisResultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnalysisResultDatabase::class.java,
                    "asclepius_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}