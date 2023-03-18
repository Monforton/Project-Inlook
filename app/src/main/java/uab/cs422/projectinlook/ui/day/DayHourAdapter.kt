package uab.cs422.projectinlook.ui.day

import android.app.ActionBar.LayoutParams
import android.content.Context
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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.util.ScreenConversion
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DayHourAdapter(val eventData: List<CalEvent>) :
    RecyclerView.Adapter<DayHourAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hourTextView = view.findViewById<TextView>(R.id.hourText)
        val eventsLayout = view.findViewById<LinearLayout>(R.id.eventsLayout)
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
                else -> DateTimeFormatter.ofPattern("HH:mm a")
            }
        val positionAsHour = LocalDateTime.of(LocalDate.now(), LocalTime.of(position, 0))
        holder.hourTextView.text = positionAsHour.format(timeFormat)
        if (LocalDateTime.now().hour == position) {
            val typedValue = TypedValue()
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
        }

        var eventTextView: TextView
        for ((count, j) in eventData.withIndex()) {
            if (j.time.hour == positionAsHour.hour) {
                if (holder.eventsLayout.childCount > 2) {
                    (holder.eventsLayout.getChildAt(2) as TextView).text =
                        holder.eventsLayout.context.getString(R.string.excess_events, count - 2)
                } else {
                    eventTextView = eventBox(j.title, holder.eventsLayout.context)
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
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.gravity = Gravity.CENTER
        textView.maxLines = 1
        textView.setPaddingRelative(
            ScreenConversion.dpToPx(textView.context, 3),
            0,
            0,
            ScreenConversion.dpToPx(textView.context, 2)
        )

        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnPrimaryContainer,
            typedValue,
            true
        )
        textView.setTextColor(typedValue.data)

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.setOnClickListener {
            Snackbar.make(it, "Clicked on event", 5000).show()
        }
        return textView
    }


}