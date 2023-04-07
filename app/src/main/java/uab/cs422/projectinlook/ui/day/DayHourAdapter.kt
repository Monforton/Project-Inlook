package uab.cs422.projectinlook.ui.day

import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.content.Context
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
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.util.dpToPx
import uab.cs422.projectinlook.util.runOnIO
import uab.cs422.projectinlook.util.hourFormatter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DayHourAdapter(private val fragment: DayFragment, private var eventData: List<CalEvent>) :
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
        val positionAsHour = LocalDateTime.of(LocalDate.now(), LocalTime.of(position, 0))
        holder.hourTextView.text = positionAsHour.format(hourFormatter(hourTVContext))
        holder.hourTextView.layoutParams = ViewGroup.LayoutParams(
            (hourTVContext.resources.displayMetrics.widthPixels / 5).toFloat()
                .coerceAtLeast(Paint().measureText(holder.hourTextView.text.toString())).toInt(),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
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
            holder.hourTextView.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
            hourTVContext.theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnBackground,
                typedValue,
                true
            )
            holder.hourTextView.setTextColor(typedValue.data)
        }

        var eventTextView: TextView
        for ((count, j) in eventData.withIndex()) {
            if (j.startHour <= positionAsHour.hour && j.endHour >= positionAsHour.hour) {
                if (holder.eventsLayout.childCount > 2) { // Event box for more than 3 events
                    (holder.eventsLayout.getChildAt(2) as TextView).text =
                        holder.eventsLayout.context.getString(R.string.excess_events, count - 2)
                } else { // Event box for singular event
                    eventTextView = eventBox(j, holder.eventsLayout.context)
                    val eventTVContext = eventTextView.context
                    eventTextView.setOnClickListener {
                        val builder = AlertDialog.Builder(eventTVContext)
                        builder.setTitle(eventTVContext.getString(R.string.dialog_title_edit_event))
                        builder.setMessage(j.title + ": " + j.desc)
                        builder.setNegativeButton(eventTVContext.getString(R.string.dialog_delete_button)) { dialog, _ ->
                            runOnIO {
                                fragment.dao.deleteEvent(j)
                            }
                            fragment.updateEvents()
                            dialog.dismiss()
                        }
                        builder.setNeutralButton(eventTVContext.getString(R.string.dialog_neutral_button)) { dialog, _ -> dialog.dismiss() }
                        builder.show()
                    }
                    holder.eventsLayout.addView(eventTextView)
                }
            }
        }
    }

    private fun eventBox(event: CalEvent, context: Context): TextView {
        val textView = TextView(context)
        textView.text = event.title
        textView.layoutParams =
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
        textView.background = AppCompatResources.getDrawable(
            textView.context,
            R.drawable.event_back
        )
        val backgroundColor: Int = Color.valueOf(event.colorR, event.colorG, event.colorB, event.colorA).toArgb()
        textView.background.setTint(backgroundColor)
        val typedValue = TypedValue()
        textView.setTextColor(
            ColorUtils.blendARGB(
                typedValue.data,
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

    fun updateDisplayedData(newData: List<CalEvent>) {
        eventData = newData
        notifyDataSetChanged()
    }

}