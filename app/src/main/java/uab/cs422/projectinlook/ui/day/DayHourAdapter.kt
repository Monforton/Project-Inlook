package uab.cs422.projectinlook.ui.day

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
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
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.dialogs.EventsViewDialogFragment
import uab.cs422.projectinlook.util.dpToPx
import uab.cs422.projectinlook.util.hourFormatter
import java.time.LocalDateTime


class DayHourAdapter(private val fragment: DayFragment, private var eventData: List<CalEvent>, private var day: LocalDateTime) :
    RecyclerView.Adapter<DayHourAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hourTextView: TextView = view.findViewById(R.id.hourText)
        val eventsLayout: LinearLayout = view.findViewById(R.id.eventsLayout)
        val cellLayout: ConstraintLayout = view.findViewById(R.id.hour_cell_layout)
    }
    private val MAX_EVENTS = 4 - 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.cell_hour, parent, false)
    )

    override fun getItemCount() = 24

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourTVContext = holder.hourTextView.context
        val positionAsHour = day.withHour(position).withMinute(0)
        // Set the hour text
        holder.hourTextView.text = positionAsHour.format(hourFormatter(hourTVContext, false))
        holder.hourTextView.layoutParams = ViewGroup.LayoutParams(
            (hourTVContext.resources.displayMetrics.widthPixels / 5).toFloat()
                .coerceAtLeast(Paint().measureText(holder.hourTextView.text.toString())).toInt(),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Set current hour as different color
        val typedValue = TypedValue()
        if (positionAsHour.hour == day.hour) {
            holder.cellLayout.context.theme.resolveAttribute(
                com.google.android.material.R.attr.colorTertiaryContainer,
                typedValue,
                true
            )
            holder.cellLayout.setBackgroundColor(typedValue.data)
            hourTVContext.theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnTertiaryContainer,
                typedValue,
                true
            )
            holder.hourTextView.setTextColor(typedValue.data)
        } else { // I don't know why this else is necessary, but otherwise it will might highlight
            holder.cellLayout.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
            hourTVContext.theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnBackground,
                typedValue,
                true
            )
            holder.hourTextView.setTextColor(typedValue.data)
        }
        // Clear and add events to the hour as TextViews
        holder.eventsLayout.removeAllViews()
        var eventTextView: TextView
        for ((count, event) in eventData.withIndex()) {
            val start = event.getStartAsLocalDateTime()
            val end = event.getEndAsLocalDateTime()
            if ((start.isBefore(positionAsHour) || start.isEqual(positionAsHour)) &&
                (end.isAfter(positionAsHour) || end.isEqual(positionAsHour))
            ) {
                if (holder.eventsLayout.childCount > MAX_EVENTS) {
                    val compressedEvent = holder.eventsLayout.getChildAt(MAX_EVENTS) as TextView
                    compressedEvent.text = holder.eventsLayout.context.getString(R.string.excess_events, count - MAX_EVENTS)
                    compressedEvent.setOnClickListener {
                        val dialog = EventsViewDialogFragment(day.withHour(position), EventsViewDialogFragment.HOUR_DISPLAY)
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
                } else { // Event box for singular event
                    eventTextView = eventBox(event, holder.eventsLayout.context)
                    eventTextView.setOnClickListener {
                        val intent = Intent(fragment.requireContext(), EditEventActivity::class.java)
                        intent.putExtra("event_data", event)
                        fragment.startActivity(intent)
                    }
                    holder.eventsLayout.addView(eventTextView)
                }
            }
        }
    }

    /**
     * Returns a TextView in the desired style for the Day view
     */
    private fun eventBox(event: CalEvent, context: Context): TextView {
        val textView = TextView(context)
        textView.text = event.title
        textView.layoutParams =
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
        textView.background = AppCompatResources.getDrawable(
            textView.context,
            R.drawable.background_for_event
        )
        val backgroundColor: Int = Color.valueOf(event.colorR, event.colorG, event.colorB, event.colorA).toArgb()
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
        textView.maxLines = 1
        textView.setPaddingRelative(
            dpToPx(textView.context, 3),
            0,
            0,
            dpToPx(textView.context, 2)
        )

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        textView.typeface = Typeface.DEFAULT_BOLD

        return textView
    }

    /**
     * Updates the RecyclerView with new data
     */
    fun updateDisplayedData(newData: List<CalEvent>, day: LocalDateTime) {
        eventData = newData
        this.day = day
        notifyDataSetChanged()
    }

}