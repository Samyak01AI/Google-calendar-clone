package com.example.googlecalendarapplication

import android.os.Bundle
import android.text.format.DateFormat // Import this for 24h format check
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.googlecalendarapplication.databinding.BottomSheetAddEventBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.ZoneId
import java.time.ZoneOffset
class AddEventBottomSheet(
    private val initialDate: LocalDate, // Renamed from selectedDate to initialDate
    private val onSaveClick: (Event) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddEventBinding

    private var currentSelectedDate: LocalDate = initialDate
    private var currentSelectedTime: LocalTime = LocalTime.now()


    // Default time: Current time or 10:00 AM
    private var selectedTime: LocalTime = LocalTime.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize UI
        updateDateText()
        updateTimeText()

        // 2. Setup DATE Picker Click
        binding.tvDateStart.setOnClickListener {
            showDatePicker()
        }

        // 3. Setup TIME Picker Click
        binding.tvTimeStart.setOnClickListener {
            showTimePicker()
        }

        // 4. Save Logic
        binding.btnSaveEvent.setOnClickListener {
            val title = binding.etEventTitle.text.toString()
            if (title.isBlank()) {
                binding.etEventTitle.error = "Title required"
                return@setOnClickListener
            }

            // Combine the separate Date and Time into one object
            val startDateTime = LocalDateTime.of(currentSelectedDate, currentSelectedTime)
            val endDateTime = startDateTime.plusHours(1)

            val newEvent = Event(
                title = title,
                startTime = startDateTime,
                endTime = endDateTime,
                colorHex = "#33B679"
            )

            onSaveClick(newEvent)
            dismiss()
        }
    }

    private fun showDatePicker() {
        // MaterialDatePicker uses Long (milliseconds), so we must convert
        val dateInMillis = currentSelectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(dateInMillis)
            .build()

        datePicker.show(parentFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener { selection ->
            // Convert the selected Long back to LocalDate
            // selection is in UTC
            currentSelectedDate = java.time.Instant.ofEpochMilli(selection)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()

            updateDateText()
        }
    }

    private fun updateDateText() {
        // Format: "Mon, Jan 18"
        val formatter = DateTimeFormatter.ofPattern("EEE, MMM d")
        binding.tvDateStart.text = currentSelectedDate.format(formatter)
    }

    private fun showTimePicker() {
        // Check if phone settings use 24-hour format
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(selectedTime.hour)
            .setMinute(selectedTime.minute)
            .setTitleText("Select time")
            .build()

        picker.show(parentFragmentManager, "TimePicker")

        picker.addOnPositiveButtonClickListener {
            // Update the variable
            selectedTime = LocalTime.of(picker.hour, picker.minute)
            // Update the UI
            updateTimeText()
        }
    }

    private fun updateTimeText() {
        // Formats as "10:00 AM" or "14:30" depending on time
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        binding.tvTimeStart.text = selectedTime.format(formatter)
    }
}