package com.example.googlecalendarapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.googlecalendarapplication.databinding.FragmentMonthBinding
import com.example.googlecalendarapplication.repository.EventRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class MonthFragment : Fragment(), CalendarAdapter.OnItemListener {

    private var _binding: FragmentMonthBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentMonthDate: LocalDate
    private lateinit var calendarAdapter: CalendarAdapter
    private var allEventsList: List<Event> = emptyList()

    // FIX: Pass 'requireActivity().application' to the Factory
    // This ensures the ViewModel gets the REAL system application context for Alarms
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory(
            EventRepository(AppDatabase.getDatabase(requireContext()).eventDao()),
            requireActivity().application
        )
    }

    companion object {
        private const val ARG_DATE = "date"
        fun newInstance(date: LocalDate): MonthFragment {
            val fragment = MonthFragment()
            val args = Bundle()
            args.putSerializable(ARG_DATE, date)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentMonthDate = arguments?.getSerializable(ARG_DATE) as? LocalDate ?: LocalDate.now()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendar()
        observeEvents()
    }

    private fun setupCalendar() {
        // 1. Generate days using your utility
        val daysInMonth = CalendarUtils.daysInMonthArray(currentMonthDate)

        // 2. Initialize Adapter with EMPTY event list initially
        calendarAdapter = CalendarAdapter(daysInMonth, currentMonthDate, emptyList(), this)

        // 3. Setup RecyclerView
        binding.calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarAdapter
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Collect flow from ViewModel
            eventViewModel.allEvents.collectLatest { events ->
                allEventsList = events
                // Update the adapter so red/green dots appear on the grid
                calendarAdapter.updateEvents(events)
            }
        }
    }

    override fun onItemClick(position: Int, dayText: String?) {
        if (dayText.isNullOrEmpty()) return

        val day = dayText.toInt()

        // Safety: Ensure we construct a valid date
        val clickedDate = try {
            currentMonthDate.withDayOfMonth(day)
        } catch (e: Exception) {
            return
        }

        // Filter events for just this day to show in the bottom sheet
        val dayEvents = allEventsList.filter {
            it.startTime.toLocalDate() == clickedDate
        }.sortedBy { it.startTime }.toMutableList()

        // Pass the deletion callback to the bottom sheet
        val bottomSheet = DayEventsBottomSheet(clickedDate, dayEvents) { eventToDelete ->
            eventViewModel.deleteEvent(eventToDelete)
            Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show()
        }

        bottomSheet.show(parentFragmentManager, "DayEvents")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}