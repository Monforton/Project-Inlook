package uab.cs422.projectinlook.ui.week

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.EditEventActivity
import uab.cs422.projectinlook.MainActivity
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.dialogs.EventsViewDialogFragment
import uab.cs422.projectinlook.util.dpToPx
import uab.cs422.projectinlook.util.hourFormatter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class WeekDayAdapter(
    private val fragment: WeekFragment,
    private var eventData: List<List<CalEvent>>,
    private val weekDays: List<LocalDateTime>
) :
    RecyclerView.Adapter<WeekDayAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hourTV: TextView = view.findViewById(R.id.week_timeText)
        val frames: List<LinearLayout> = listOf(
            view.findViewById(R.id.week_frame1),
            view.findViewById(R.id.week_frame2),
            view.findViewById(R.id.week_frame3),
            view.findViewById(R.id.week_frame4),
            view.findViewById(R.id.week_frame5),
            view.findViewById(R.id.week_frame6),
            view.findViewById(R.id.week_frame7)
        )
        val layout: ConstraintLayout = view.findViewById(R.id.week_cell_layout)
    }
    private val MAX_EVENTS = 2 - 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.cell_weekday, parent, false)
    )

    override fun getItemCount() = 24

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val today = LocalDateTime.now()
        val hourTVContext = holder.hourTV.context
        val positionAsHour = LocalDateTime.of(LocalDate.now(), LocalTime.of(position, 0))
        holder.hourTV.text = positionAsHour.format(hourFormatter(holder.hourTV.context, false))
        val typedValue = TypedValue()
        if ((today.isAfter(weekDays[0]) || today.isEqual(weekDays[0])) &&
            (today.isBefore(weekDays[6]) || today.isEqual(weekDays[6]))
        ) {
            if (positionAsHour.hour == today.hour) {
                holder.layout.context.theme.resolveAttribute(
                    com.google.android.material.R.attr.colorTertiaryContainer,
                    typedValue,
                    true
                )
                holder.layout.setBackgroundColor(typedValue.data)
                hourTVContext.theme.resolveAttribute(
                    com.google.android.material.R.attr.colorOnTertiaryContainer,
                    typedValue,
                    true
                )
                holder.hourTV.setTextColor(typedValue.data)
            } else { // I don't know why this else is necessary, but otherwise it will might highlight
                holder.layout.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
                hourTVContext.theme.resolveAttribute(
                    com.google.android.material.R.attr.colorOnBackground,
                    typedValue,
                    true
                )
                holder.hourTV.setTextColor(typedValue.data)
            }
        }
        var eventTextView: TextView

        for ((i, day) in eventData.withIndex()) { // This is the count for each day's data
            holder.frames[i].removeAllViews()
            for ((j, event) in day.withIndex()) {// This is the count for each event of the current day
                val start = event.getStartAsLocalDateTime()
                val end = event.getEndAsLocalDateTime()
                val posHourRealTime = weekDays[i].withHour(position)
                if ((start.isBefore(posHourRealTime) || start.isEqual(posHourRealTime)) &&
                    (end.isAfter(posHourRealTime) || end.isEqual(posHourRealTime))
                ) {
                    if (holder.frames[i].childCount > MAX_EVENTS) {
                        val compressedEvent = holder.frames[i].getChildAt(MAX_EVENTS) as TextView
                        compressedEvent.text = holder.frames[i].context.getString(R.string.excess_events, j - MAX_EVENTS)
                        compressedEvent.setOnClickListener {
                            val dialog = EventsViewDialogFragment(posHourRealTime, EventsViewDialogFragment.HOUR_DISPLAY)
                            dialog.show(fragment.childFragmentManager, "CustomDialog")
                        }
                        compressedEvent.context.theme.resolveAttribute(
                            com.google.android.material.R.attr.colorSecondaryContainer,
                            typedValue, true
                        )
                        compressedEvent.background.setTint(typedValue.data)
                        compressedEvent.context.theme.resolveAttribute(
                            com.google.android.material.R.attr.colorOnSecondaryContainer,
                            typedValue, true
                        )
                        compressedEvent.setTextColor(typedValue.data)
                    } else {
                        eventTextView = eventBox(event, holder.frames[i].context)
                        eventTextView.setOnClickListener {
                            val intent = Intent(fragment.requireContext(), EditEventActivity::class.java)
                            intent.putExtra("event_data", event)
                            fragment.startActivity(intent)
                            (fragment.activity as MainActivity).selectedDay = weekDays[i]
                        }
                        holder.frames[i].addView(eventTextView)
                    }
                }
            }
        }
    }

    private fun eventBox(event: CalEvent, context: Context): TextView {
        val textView = TextView(context)
        textView.text = event.title
        textView.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        textView.background = AppCompatResources.getDrawable(
            textView.context,
            R.drawable.background_for_event
        )
        val backgroundColor: Int =
            Color.valueOf(event.colorR, event.colorG, event.colorB, event.colorA).toArgb()
        textView.background.setTint(backgroundColor)
        textView.setTextColor(
            ColorUtils.blendARGB(
                backgroundColor,
                if (Color.luminance(backgroundColor) > 0.5) Color.BLACK else Color.WHITE,
                0.95f
            )
        )
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.gravity = Gravity.CENTER
        textView.maxLines = 2
        textView.setPaddingRelative(
            dpToPx(textView.context, 3),
            0,
            0,
            dpToPx(textView.context, 2)
        )

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        textView.typeface = Typeface.DEFAULT_BOLD

        return textView
    }

    fun updateWeekRecView(newData: List<List<CalEvent>>) {
        eventData = newData
        notifyDataSetChanged()
    }
}