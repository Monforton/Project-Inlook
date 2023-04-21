package uab.cs422.projectinlook.ui.dialogs

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.EditEventActivity
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.util.hourFormatter
import uab.cs422.projectinlook.util.intAsHour
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EventsViewEventsAdapter(
    private val dialog: EventsViewDialogFragment,
    private var eventData: List<CalEvent>,
    private var time: LocalDateTime
) :
    RecyclerView.Adapter<EventsViewEventsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeframeTV: TextView = view.findViewById(R.id.events_view_cell_timeframe_text)
        val eventTV: TextView = view.findViewById(R.id.events_view_cell_event_text)
        val layout: LinearLayout = view.findViewById(R.id.events_view_cell_linear_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.cell_events_view, parent, false)
    )

    override fun getItemCount() = eventData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the timeframe of event
        val event = eventData[position]
        holder.timeframeTV.text =
            if (event.getStartAsLocalDateTime().isBefore(
                    LocalDateTime.of(time.toLocalDate(), LocalTime.MIN)
                )
            ) {
                if (event.getEndAsLocalDateTime().isAfter(
                        LocalDateTime.of(time.toLocalDate(), LocalTime.MAX)
                    )
                ) {
                    "Until " + event.getEndAsLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("MMM d"))
                } else {
                    "Until " + LocalDateTime.of(time.toLocalDate(), LocalTime.of(event.endHour, 0))
                        .format(hourFormatter(holder.timeframeTV.context, false))
                }
            } else {
                intAsHour(hour = event.startHour)
                    .format(hourFormatter(holder.timeframeTV.context, false)) + " - " +
                        if (event.getEndAsLocalDateTime()
                                .isBefore(LocalDateTime.of(time.toLocalDate(), LocalTime.MAX))
                        ) {
                            intAsHour(hour = event.endHour).format(
                                hourFormatter(
                                    holder.timeframeTV.context,
                                    false
                                )
                            )
                        } else {
                            event.getEndAsLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("MMM d"))
                        }
            }
//            if (event.getStartAsLocalDateTime().isBefore(time.withMinute(0))) {
//                if (event.getEndAsLocalDateTime()
//                        .isAfter(LocalDateTime.of(LocalDate.now(), LocalTime.MAX))
//                ) {
//                    "Until " + event.getEndAsLocalDateTime()
//                        .format(DateTimeFormatter.ofPattern("MMM d"))
//                } else {
//                    "Until " + LocalDateTime.of(LocalDate.now(), LocalTime.of(event.endHour, 0))
//                        .format(hourFormatter(holder.timeframeTV.context, false))
//                }
//            } else {
//                intAsHour(hour = event.startHour)
//                    .format(hourFormatter(holder.timeframeTV.context, false)) + " - " + intAsHour(
//                    hour = event.endHour
//                )
//                    .format(hourFormatter(holder.timeframeTV.context, false))
//            }
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
        holder.layout.setOnClickListener {
            val intent = Intent(
                dialog.requireParentFragment().requireContext(),
                EditEventActivity::class.java
            )
            intent.putExtra("event_data", event)
            dialog.requireParentFragment().startActivity(intent)
            dialog.dismiss()
        }
    }
}