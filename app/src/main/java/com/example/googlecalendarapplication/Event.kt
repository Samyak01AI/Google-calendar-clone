package com.example.googlecalendarapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 0 triggers auto-generation

    val title: String,

    val location: String? = null,

    val description: String? = null,

    // We use LocalDateTime here, handled by our Converter
    val startTime: LocalDateTime,

    val endTime: LocalDateTime,

    // For "All Day" events, time is ignored in UI logic
    val isAllDay: Boolean = false,

    // Store color as an Integer resource ID (R.color.sage) or Hex String
    val colorHex: String = "#33B679" // Default to Sage Green
)