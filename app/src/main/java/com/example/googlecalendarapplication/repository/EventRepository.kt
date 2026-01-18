package com.example.googlecalendarapplication.repository

import com.example.googlecalendarapplication.Event
import com.example.googlecalendarapplication.EventDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

class EventRepository(private val eventDao: EventDao) {

    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    suspend fun insert(event: Event): Long {
        return eventDao.insertEvent(event)
    }
    suspend fun delete(event: Event) {
        eventDao.deleteEvent(event)
    }
    // Helper to get events for a specific month only (Performance Optimization)
    fun getEventsForMonth(date: LocalDate): Flow<List<Event>> {
        val startOfMonth = date.withDayOfMonth(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        val endOfMonth = date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC)
        return eventDao.getEventsForDateRange(startOfMonth, endOfMonth)
    }
}