package uab.cs422.projectinlook.ui.month

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

class MonthAdapter(
    private val fragment: MonthFragment,
    private val day: LocalDate
) : RecyclerView.Adapter<MonthAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val dayOfMonth: TextView = view.findViewById(R.id.cellDayText)
    }

    private var daysOfMonth = daysInMonthArray(day)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.cell_month, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dayOfMonth.text = daysOfMonth[position]
    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }

    fun updateEventRecycler() {
        daysOfMonth = daysInMonthArray(day)
    }

    private fun daysInMonthArray(date: LocalDate?): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = day.withDayOfMonth(1)
        val dayOfWeek = when (PreferenceManager.getDefaultSharedPreferences(fragment.requireContext())
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
