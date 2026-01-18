package com.example.googlecalendarapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.googlecalendarapplication.repository.EventRepository
import com.example.googlecalendarapplication.utils.AlarmScheduler
import kotlinx.coroutines.launch

class EventViewModel(
    private val repository: EventRepository,
    application: Application
) : AndroidViewModel(application) {

    val allEvents = repository.allEvents
    private val alarmScheduler = AlarmScheduler(application)

    fun insert(event: Event) = viewModelScope.launch {
        val newId = repository.insert(event)
        val savedEvent = event.copy(id = newId)
        alarmScheduler.scheduleAlarm(savedEvent)
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        alarmScheduler.cancelAlarm(event)
        repository.delete(event)
    }
}

class EventViewModelFactory(
    private val repository: EventRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}