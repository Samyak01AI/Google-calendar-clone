package com.example.googlecalendarapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class CalendarAdapter(
    private val days: ArrayList<LocalDate?>,
    private val selectedMonth: LocalDate,
    private var events: List<Event>, // <--- CHANGE val TO var
    private val onItemListener: OnItemListener
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_calendar_day, parent, false)

        // DYNAMIC HEIGHT Logic
        val layoutParams = view.layoutParams
        if (layoutParams.height < 100) {
            layoutParams.height = (parent.height * 0.166666666).toInt()
        }

        return CalendarViewHolder(view, onItemListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        // FIX 1: Define 'date' first!
        val date = days[position]

        if (date == null) {
            holder.dayOfMonth.text = ""
            holder.eventContainer.removeAllViews() // Clear views even for empty days
            return
        }

        holder.dayOfMonth.text = date.dayOfMonth.toString()

        // --- VISUAL LOGIC FOR DATE TEXT ---
        if (date == LocalDate.now()) {
            holder.dayOfMonth.setBackgroundResource(R.drawable.bg_today_circle)
            holder.dayOfMonth.setTextColor(Color.WHITE)
        } else {
            holder.dayOfMonth.background = null
            if (date.month != selectedMonth.month) {
                holder.dayOfMonth.setTextColor(Color.parseColor("#B0B0B0"))
                holder.dayOfMonth.alpha = 0.5f
            } else {
                holder.dayOfMonth.setTextColor(Color.parseColor("#3C4043"))
                holder.dayOfMonth.alpha = 1.0f
            }
        }

        // --- EVENT LOGIC ---

        // FIX 2: Clear previous views!
        // If we don't do this, recycled views will keep adding bars on top of old ones.
        holder.eventContainer.removeAllViews()

        val eventsForDay = events.filter {
            it.startTime.toLocalDate() == date
        }

        if (eventsForDay.isNotEmpty()) {
            // FIX 3: Context access via holder.itemView.context
            val context = holder.itemView.context

            eventsForDay.take(3).forEach { event ->
                val view = View(context)
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    12 // Height in pixels (approx 4dp)
                )
                params.setMargins(0, 4, 0, 4) // Add margin so bars don't touch
                view.layoutParams = params

                try {
                    view.setBackgroundColor(Color.parseColor(event.colorHex))
                } catch (e: Exception) {
                    view.setBackgroundColor(Color.LTGRAY) // Fallback if hex is invalid
                }

                holder.eventContainer.addView(view)
            }
        }
    }

    override fun getItemCount(): Int = days.size

    class CalendarViewHolder(
        itemView: View,
        private val onItemListener: OnItemListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val dayOfMonth: TextView = itemView.findViewById(R.id.tvDayText)
        val eventContainer: LinearLayout = itemView.findViewById(R.id.eventContainer)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            // Pass the actual date text if available
            onItemListener.onItemClick(adapterPosition, dayOfMonth.text.toString())
        }
    }

    interface OnItemListener {
        fun onItemClick(position: Int, dayText: String?)
    }

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged() // Refreshes the grid with new data
    }
}