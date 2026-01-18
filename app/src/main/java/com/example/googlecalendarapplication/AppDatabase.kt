package com.example.googlecalendarapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Event::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Register the converter here
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    // Standard Singleton Pattern to ensure only one DB instance exists
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calendar_database"
                )
                    .fallbackToDestructiveMigration() // Wipes data if you change schema (dev only)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}