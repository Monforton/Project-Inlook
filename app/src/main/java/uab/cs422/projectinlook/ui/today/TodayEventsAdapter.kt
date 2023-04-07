package uab.cs422.projectinlook.ui.today

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.util.getCalEventAsLocalDateTime
import uab.cs422.projectinlook.util.hourFormatter
import uab.cs422.projectinlook.util.intAsHour
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TodayEventsAdapter(
    private val fragment: TodayFragment,
    private var eventData: List<CalEvent>
) :
    RecyclerView.Adapter<TodayEventsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeframeTV: TextView = view.findViewById(R.id.today_timeTV)
        val eventTV: TextView = view.findViewById(R.id.today_eventTV)
        val layout: ConstraintLayout = view.findViewById(R.id.today_cell_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.cell_today, parent, false)
    )

    override fun getItemCount() = eventData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the timeframe of event
        val event = eventData[position]
        holder.timeframeTV.text =
            if (getCalEventAsLocalDateTime(event = event).isBefore(
                    LocalDateTime.of(
                        LocalDate.now(),
                        LocalTime.MIN
                    )
                )
            ) {
                if (getCalEventAsLocalDateTime(event = event).isAfter(
                        LocalDateTime.of(
                            LocalDate.now(),
                            LocalTime.MAX
                        )
                    )
                ) "Thru Today"
                "Until " + LocalDateTime.of(LocalDate.now(), LocalTime.of(event.endHour, 0))
                    .format(hourFormatter(holder.timeframeTV.context))
            } else {
                intAsHour(hour = event.startHour)
                    .format(hourFormatter(holder.timeframeTV.context)) + " - " + intAsHour(hour = event.endHour)
                    .format(hourFormatter(holder.timeframeTV.context))
            }
        holder.eventTV.text = event.title
        holder.layout.background =
            Color.valueOf(event.colorR, event.colorG, event.colorB, event.colorA).toDrawable()

    }

    fun updateDisplayedData(newData: List<CalEvent>) {
        eventData = newData
        notifyDataSetChanged()
    }
}