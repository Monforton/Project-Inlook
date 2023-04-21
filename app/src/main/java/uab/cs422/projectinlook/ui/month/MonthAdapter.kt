package uab.cs422.projectinlook.ui.month

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.EventDao
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.dialogs.EventsViewDialogFragment
import uab.cs422.projectinlook.util.runOnIO
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

class MonthAdapter(
    private val fragment: MonthFragment,
    private val day: LocalDate,
    private var dao: EventDao
) : RecyclerView.Adapter<MonthAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val cellLayout: ConstraintLayout = view.findViewById(R.id.month_cellLayout)
        val dayText: TextView = view.findViewById(R.id.month_cellDayText)
        val eventDotsLayout: LinearLayout = view.findViewById(R.id.month_eventDotsLayout)
    }

    private var daysOfMonth = daysInMonthArray(day)
    private val MAX_EVENTS = 3 - 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.cell_month, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dayText.text = daysOfMonth[position]
        val posAsDay: LocalDate
        if (daysOfMonth[position].isNotEmpty()) {
            posAsDay = day.withDayOfMonth(daysOfMonth[position].toInt())
            // If the position is today's date, highlight it
            if (posAsDay.isEqual(LocalDate.now())) {
                val typedValue = TypedValue()
                holder.cellLayout.context.theme.resolveAttribute(
                    com.google.android.material.R.attr.colorTertiaryContainer,
                    typedValue, true
                )
                holder.cellLayout.backgroundTintList = ColorStateList.valueOf(typedValue.data)
                holder.dayText.context.theme.resolveAttribute(
                    com.google.android.material.R.attr.colorOnTertiaryContainer,
                    typedValue, true
                )
                holder.dayText.setTextColor(typedValue.data)
            } else {
                holder.cellLayout.backgroundTintList = ColorStateList.valueOf(0)
            }

            var events: List<CalEvent> = listOf()
            runOnIO {
                events = dao.getEventsOfDay(posAsDay.dayOfMonth, posAsDay.monthValue, posAsDay.year)
            }
            holder.eventDotsLayout.removeAllViews()
            for ((count, event) in events.withIndex()) {
                if (count > MAX_EVENTS) {
                    val dotView = (holder.eventDotsLayout[MAX_EVENTS] as ImageView)
                    dotView.setImageDrawable(
                        AppCompatResources.getDrawable(
                            dotView.context,
                            R.drawable.ic_circle_plus_24
                        )
                    )
                    val typedValue = TypedValue()
                    dotView.context.theme.resolveAttribute(
                        com.google.android.material.R.attr.colorOnSecondaryContainer,
                        typedValue, true
                    )
                    dotView.drawable.setTint(typedValue.data)
                } else {
                    holder.eventDotsLayout.addView(eventDot(event, holder.eventDotsLayout.context))
                }
            }
            holder.cellLayout.setOnClickListener {
                val dialog = EventsViewDialogFragment(LocalDateTime.of(posAsDay, LocalTime.MIN), EventsViewDialogFragment.DAY_DISPLAY)
                dialog.show(fragment.childFragmentManager, "CustomDialog")
            }

        }
    }

    override fun getItemCount() = daysOfMonth.size

    fun updateEventRecycler() {
        daysOfMonth = daysInMonthArray(day)
        notifyDataSetChanged()
    }

    private fun eventDot(event: CalEvent, context: Context): ImageView {
        val dotIV = ImageView(context)
        dotIV.setImageDrawable(
            AppCompatResources.getDrawable(
                dotIV.context,
                R.drawable.ic_circle_24
            )
        )
        dotIV.drawable.setTint(event.getColorAsColor().toArgb())
        dotIV.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        return dotIV
    }

    private fun daysInMonthArray(date: LocalDate?): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = day.withDayOfMonth(1)
        val dayOfWeek =
            when (PreferenceManager.getDefaultSharedPreferences(fragment.requireContext())
                .getString("first_day", "")) {
                "local" -> firstOfMonth.get(WeekFields.of(Locale.getDefault()).dayOfWeek())
                "sat" -> firstOfMonth.get(WeekFields.of(DayOfWeek.SATURDAY, 1).dayOfWeek())
                "sun" -> firstOfMonth.get(WeekFields.of(DayOfWeek.SUNDAY, 1).dayOfWeek())
                "mon" -> firstOfMonth.get(WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek())
                else -> firstOfMonth.get(WeekFields.of(Locale.getDefault()).dayOfWeek())
            } - 1

        var i = 1
        while (i <= 42) {
            if (i == 1 && 7 <= dayOfWeek) {
                i = 8
            }
            if (i == 36 && 36 > daysInMonth + dayOfWeek) {
                break
            }
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
            i++
        }
        return daysInMonthArray
    }
}
