package uab.cs422.projectinlook.ui.day

import android.app.ActionBar.LayoutParams
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
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.util.ScreenConversion
import java.time.format.DateTimeFormatter

class DayHourAdapter(val eventData: List<CalEvent>): RecyclerView.Adapter<DayHourAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val hourTextView = view.findViewById<TextView>(R.id.hourText)
        val eventsLayout = view.findViewById<LinearLayout>(R.id.eventsLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.cell_hour, parent, false)
    )

    override fun getItemCount() = eventData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        println(PreferenceManager.getDefaultSharedPreferences(holder.hourTextView.context).getString("hour_format", ""))
        when (PreferenceManager.getDefaultSharedPreferences(holder.hourTextView.context).getString("hour_format", "")) {
            "clock_time_format" -> timeFormat = DateTimeFormatter.ofPattern("h:mm a")
            "day_time_format" -> timeFormat = DateTimeFormatter.ofPattern("HH:mm")
            else -> DateTimeFormatter.ofPattern("HH:mm a")
        }
        holder.hourTextView.text = eventData[position].time.format(timeFormat)
        val totalSize = eventData.size
        for (i in 0..2) {
            val eventTextView = TextView(holder.eventsLayout.context)
            if (totalSize > 3 && i == 2) {
                eventTextView.text = holder.eventsLayout.context.getString(R.string.excess_events, (totalSize - i))
            } else {
                eventTextView.text = eventData[i].title
            }

            eventTextView.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
            eventTextView.background = AppCompatResources.getDrawable(eventTextView.context,
                R.drawable.event_back
            )
            eventTextView.ellipsize = TextUtils.TruncateAt.END
            eventTextView.gravity = Gravity.CENTER
            eventTextView.maxLines = 1
            eventTextView.setPaddingRelative(
                ScreenConversion.dpToPx(eventTextView.context, 3),
                0,
                0,
                ScreenConversion.dpToPx(eventTextView.context, 2))
            eventTextView.setTextColor(ContextCompat.getColor(eventTextView.context, R.color.md_theme_light_onPrimaryContainer))
            eventTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            eventTextView.typeface = Typeface.DEFAULT_BOLD
            eventTextView.setOnClickListener {
                Snackbar.make(it, "Clicked on event", 5000).show()
            }
            holder.eventsLayout.addView(eventTextView)
        }
    }

}