package com.example.googlecalendarapplication
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googlecalendarapplication.databinding.ItemEventRowBinding
import java.time.format.DateTimeFormatter

class EventListAdapter(private val events: List<Event>) :
    RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    inner class EventViewHolder(val binding: ItemEventRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        holder.binding.tvEventTitle.text = event.title

        // Format time: "10:00 AM - 11:00 AM"
        val startString = event.startTime.format(timeFormatter)
        val endString = event.endTime.format(timeFormatter)
        holder.binding.tvEventTime.text = "$startString - $endString"

        // Set the color strip
        try {
            holder.binding.viewColorStrip.setBackgroundColor(Color.parseColor(event.colorHex))
        } catch (e: Exception) {
            holder.binding.viewColorStrip.setBackgroundColor(Color.LTGRAY)
        }
    }

    override fun getItemCount(): Int = events.size
}