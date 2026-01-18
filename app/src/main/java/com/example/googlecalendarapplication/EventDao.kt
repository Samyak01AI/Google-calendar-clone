package com.example.googlecalendarapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    // Using Flow allows the UI to update automatically when data changes
    @Query("SELECT * FROM events ORDER BY startTime ASC")
    fun getAllEvents(): Flow<List<Event>>

    // CRITICAL: Only load events for the visible range (e.g., Jan 1 to Jan 31)
    @Query("SELECT * FROM events WHERE startTime BETWEEN :startRange AND :endRange")
    fun getEventsForDateRange(startRange: Long, endRange: Long): Flow<List<Event>>

    // For clicking a specific day
    @Query("SELECT * FROM events WHERE startTime >= :startOfDay AND startTime < :endOfDay")
    fun getEventsForSingleDay(startOfDay: Long, endOfDay: Long): Flow<List<Event>>
}