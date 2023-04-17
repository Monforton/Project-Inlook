package uab.cs422.projectinlook.ui.today

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
    private var eventData: List<CalEvent>
) :
    RecyclerView.Adapter<TodayEventsAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeframeTV: TextView = view.findViewById(R.id.today_timeTV)
        val eventTV: TextView = view.findViewById(R.id.today_eventTV)
        val layout: LinearLayout = view.findViewById(R.id.today_cell_linear_layout)
        val dot: ImageView = view.findViewById(R.id.today_dot)
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
        // Finish setting color of everything
        holder.layout.backgroundTintList = ColorStateList.valueOf(
            Color.valueOf(
                event.colorR,
                event.colorG,
                event.colorB,
                event.colorA
            ).toArgb()
        )
        val backgroundColor = Color.valueOf(
            event.colorR,
            event.colorG,
            event.colorB,
            event.colorA
        ).toArgb()
        holder.eventTV.setTextColor(
            if (Color.luminance(backgroundColor) > 0.5) Color.BLACK else Color.WHITE
        )
        holder.timeframeTV.setTextColor(
            if (Color.luminance(backgroundColor) > 0.5) Color.BLACK else Color.WHITE
        )
        holder.dot.imageTintList =
            ColorStateList.valueOf(if (Color.luminance(backgroundColor) > 0.5) Color.BLACK else Color.WHITE)
    }

    /**
     * Updates the RecyclerView's shown data with new
     */
    fun updateDisplayedData(newData: List<CalEvent>) {
        eventData = newData
        notifyDataSetChanged()
    }
}