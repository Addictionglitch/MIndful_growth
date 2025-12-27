package com.example.mindfulgrowth.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FocusSession::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusDao(): FocusDao
}