package com.example.googlecalendarapplication

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

object CalendarUtils {

    fun daysInMonthArray(selectedDate: LocalDate): ArrayList<LocalDate?> {
        val daysInMonthArray = ArrayList<LocalDate?>()

        val yearMonth = YearMonth.from(selectedDate)
        val daysInMonth = yearMonth.lengthOfMonth()

        // Get the first day of the month (e.g., "2023-10-01")
        val firstOfMonth = selectedDate.withDayOfMonth(1)

        // Find out which day of the week the 1st falls on (Sunday=0/7, Monday=1, etc.)
        // distinct value for Sunday as start of week
        val dayOfWeek = firstOfMonth.dayOfWeek.value

        // Adjust for Sunday start (Java time uses Monday=1, Sunday=7)
        // We want Sunday to be index 0
        val startOffset = if (dayOfWeek == 7) 0 else dayOfWeek

        // 1. Padding days from Previous Month
        // We calculate backwards from the 1st
        for (i in 0 until startOffset) {
            val prevDay = firstOfMonth.minusDays((startOffset - i).toLong())
            daysInMonthArray.add(prevDay)
        }

        // 2. Current Month
        for (i in 1..daysInMonth) {
            daysInMonthArray.add(LocalDate.of(selectedDate.year, selectedDate.month, i))
        }

        // 3. Remaining days (Next Month) to fill 42 cells (6 rows x 7 cols)
        val remainingDays = 42 - daysInMonthArray.size
        val lastOfMonth = selectedDate.withDayOfMonth(daysInMonth)

        for (i in 1..remainingDays) {
            daysInMonthArray.add(lastOfMonth.plusDays(i.toLong()))
        }

        return daysInMonthArray
    }
}