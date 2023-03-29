package uab.cs422.projectinlook.ui.day

import android.app.ActionBar.LayoutParams
import android.content.Context
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
import androidx.core.graphics.ColorUtils
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.util.dpToPx
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DayHourAdapter(private val eventData: List<CalEvent>) :
    RecyclerView.Adapter<DayHourAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hourTextView: TextView = view.findViewById(R.id.hourText)
        val eventsLayout: LinearLayout = view.findViewById(R.id.eventsLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.cell_hour, parent, false)
    )

    override fun getItemCount() = 24

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourTVContext = holder.hourTextView.context
        val timeFormat: DateTimeFormatter =
            when (PreferenceManager.getDefaultSharedPreferences(hourTVContext)
                .getString("hour_format", "")) {
                "clock_time_format" -> DateTimeFormatter.ofPattern("h a")
                "day_time_format" -> DateTimeFormatter.ofPattern("H:mm")
                else -> DateTimeFormatter.ofPattern("h a")
            }
        val positionAsHour = LocalDateTime.of(LocalDate.now(), LocalTime.of(position, 0))
        holder.hourTextView.text = positionAsHour.format(timeFormat)

        val typedValue = TypedValue()
        if (positionAsHour.hour == LocalDateTime.now().hour) {
            hourTVContext.theme.resolveAttribute(
                com.google.android.material.R.attr.colorTertiaryContainer,
                typedValue,
                true
            )
            holder.hourTextView.setBackgroundColor(typedValue.data)
            hourTVContext.theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnTertiaryContainer,
                typedValue,
                true
            )
            holder.hourTextView.setTextColor(typedValue.data)
        } else { // I don't know why this else is necessary, but otherwise it will highlight if (hour - 16) > 0
            holder.hourTextView.setBackgroundColor(Color.valueOf(0f,0f,0f,0f).toArgb())
            hourTVContext.theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnBackground,
                typedValue,
                true
            )
            holder.hourTextView.setTextColor(typedValue.data)
        }

        var eventTextView: TextView
        for ((count, j) in eventData.withIndex()) {
            if (j.startHour == positionAsHour.hour) {
                if (holder.eventsLayout.childCount > 2) {
                    (holder.eventsLayout.getChildAt(2) as TextView).text =
                        holder.eventsLayout.context.getString(R.string.excess_events, count - 2)
                } else {
                    eventTextView = eventBox(j.title, holder.eventsLayout.context)
                    eventTextView.setOnClickListener {
                        Snackbar.make(it, "Day: ${j.startDayOfMonth}", 5000).show()
                    }
                    holder.eventsLayout.addView(eventTextView)
                }
            }
        }
    }

    private fun eventBox(text: String, context: Context): TextView {
        val textView = TextView(context)
        textView.text = text
        textView.layoutParams =
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
        textView.background = AppCompatResources.getDrawable(
            textView.context,
            R.drawable.event_back
        )
        val typedValue = TypedValue()
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimaryContainer,
            typedValue,
            true
        )
        textView.background.setTint(typedValue.data)

        textView.setTextColor(
            ColorUtils.blendARGB(
                typedValue.data,
                if (Color.luminance(typedValue.data) > 0.5) Color.BLACK else Color.WHITE,
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


}