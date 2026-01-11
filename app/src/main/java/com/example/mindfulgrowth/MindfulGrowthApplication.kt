package com.example.mindfulgrowth

import android.app.Application
import androidx.room.Room
import com.example.mindfulgrowth.data.AppDatabase

class MindfulGrowthApplication : Application() {
    // Lazy initialization of the database
    // This creates the database instance only when it is first accessed
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "mindful-db"
        ).build()
    }
}
