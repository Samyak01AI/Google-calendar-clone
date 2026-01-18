package com.example.googlecalendarapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.googlecalendarapplication.databinding.BottomSheetDayEventsBinding
import com.example.googlecalendarapplication.utils.SwipeToDeleteCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayEventsBottomSheet(
    private val selectedDate: LocalDate,
    private val events: MutableList<Event>, // Change to MutableList so we can remove items locally
    private val onDeleteConfirmed: (Event) -> Unit // Callback to Fragment/Activity
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetDayEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDayEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ... setup Header and Adapter ...
        val eventsAdapter = EventListAdapter(events)
        binding.rvEvents.adapter = eventsAdapter
        binding.rvEvents.layoutManager = LinearLayoutManager(context)

        // --- SWIPE TO DELETE LOGIC ---
        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val eventToDelete = events[position]

                // 1. Remove from List and Update UI immediately (Optimistic update)
                events.removeAt(position)
                eventsAdapter.notifyItemRemoved(position)

                // 2. Notify the Fragment to delete from DB
                onDeleteConfirmed(eventToDelete)

                // 3. Optional: Show "Undo" Snackbar
                // showUndoSnackbar(eventToDelete, position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.rvEvents)
    }
}