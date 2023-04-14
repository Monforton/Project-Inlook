package uab.cs422.projectinlook.ui.week

import android.app.ActionBar
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.util.dpToPx
import uab.cs422.projectinlook.util.hourFormatter
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class WeekEventAdapter(
    private val fragment: WeekFragment,
    private val eventData: List<List<CalEvent>>,
    private val weekDays: List<LocalDateTime>
) :
    RecyclerView.Adapter<WeekEventAdapter.ViewHolder>() {
    private val data = eventData.toMutableList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hourTV: TextView = view.findViewById(R.id.week_timeText)
        val frame1: LinearLayout = view.findViewById(R.id.week_frame1)
        val frame2: LinearLayout = view.findViewById(R.id.week_frame2)
        val frame3: LinearLayout = view.findViewById(R.id.week_frame3)
        val frame4: LinearLayout = view.findViewById(R.id.week_frame4)
        val frame5: LinearLayout = view.findViewById(R.id.week_frame5)
        val frame6: LinearLayout = view.findViewById(R.id.week_frame6)
        val frame7: LinearLayout = view.findViewById(R.id.week_frame7)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.cell_weekday, parent, false)
    )

    override fun getItemCount() = 24

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val today = LocalDateTime.now()
        val hourTVContext = holder.hourTV.context
        val positionAsHour = LocalDateTime.of(LocalDate.now(), LocalTime.of(position, 0))
        holder.hourTV.text = positionAsHour.format(hourFormatter(holder.hourTV.context))
        val typedValue = TypedValue()
        if ((today.isAfter(weekDays[0]) || today.isEqual(weekDays[0])) &&
            (today.isBefore(weekDays[6]) || today.isEqual(weekDays[6]))
        ) {
            if (positionAsHour.hour == today.hour) {
                hourTVContext.theme.resolveAttribute(
                    com.google.android.material.R.attr.colorTertiaryContainer,
                    typedValue,
                    true
                )
                holder.hourTV.setBackgroundColor(typedValue.data)
                hourTVContext.theme.resolveAttribute(
                    com.google.android.material.R.attr.colorOnTertiaryContainer,
                    typedValue,
                    true
                )
                holder.hourTV.setTextColor(typedValue.data)
            }
        } else { // I don't know why this else is necessary, but otherwise it will highlight if (hour - 16) > 0
            holder.hourTV.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
            hourTVContext.theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnBackground,
                typedValue,
                true
            )
            holder.hourTV.setTextColor(typedValue.data)
        }
        var eventTV: TextView
        var eventTextView: TextView
        val frames = listOf(
            holder.frame1,
            holder.frame2,
            holder.frame3,
            holder.frame4,
            holder.frame5,
            holder.frame6,
            holder.frame7
        )
        for ((i, day) in eventData.withIndex()) { // This is the count for each day's data
            for (event in day) // This is the count for each event of the current day
                if (event.startHour <= positionAsHour.hour && event.endHour >= positionAsHour.hour) {

                    eventTextView = eventBox(event, frames[i].context)
                    val eventTVContext = eventTextView.context
                    eventTextView.setOnClickListener {
                        val builder = AlertDialog.Builder(eventTVContext)
                        builder.setTitle(event.title)
                        builder.setMessage("" + event.startHour + " - " + event.endHour + ": " + event.desc)
                        builder.setNegativeButton(eventTVContext.getString(R.string.dialog_delete_button)) { dialog, _ ->
                            runOnIO {
                                fragment.dao.deleteEvent(event)
                            }
                            fragment.updateEvents()
                            dialog.dismiss()
                        }
                        builder.setNeutralButton(eventTVContext.getString(R.string.dialog_neutral_button)) { dialog, _ -> dialog.dismiss() }
                        builder.setPositiveButton(eventTVContext.getString(R.string.dialog_positive_button)) { dialog, _ ->
                            showEditDialog(eventTVContext, event)
                            dialog.dismiss()
                        }
                        builder.show()

                        frames[i].addView(eventTextView)
                    }
                }
        }
    }

    //2nd dialog opens when user selects edit
    private fun showEditDialog(context: Context, position: CalEvent) {
        val editTitle = EditText(context)
        val editDesc = EditText(context)
        //need to implement option for user to edit time of event
        //val editStartHour = EditText(context)
        //val editEndHour = EditText(context)
        editTitle.setText(position.title)
        editDesc.setText(position.desc)

        val alert = AlertDialog.Builder(context)
            .setCustomTitle(editTitle)
            .setView(editDesc)

            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Done") { dialog, _ ->
                position.title = editTitle.text.toString()
                position.desc = editDesc.text.toString()
                //notifyItemChanged()
                runOnIO {
                    fragment.dao.updateEvent(position)
                }
                fragment.updateEvents()
                dialog.dismiss()
            }
            .create()
        alert.show()
    }

    private fun eventBox(event: CalEvent, context: Context): TextView {
        val textView = TextView(context)
        textView.text = event.title
        textView.layoutParams =
            LinearLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                1f
            )
        textView.background = AppCompatResources.getDrawable(
            textView.context,
            R.drawable.event_back
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

    fun updateWeekRecView(newData: List<List<CalEvent>>) {
        data.addAll(newData)
        notifyDataSetChanged()
    }
}