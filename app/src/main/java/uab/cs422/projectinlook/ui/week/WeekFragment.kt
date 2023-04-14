package uab.cs422.projectinlook.ui.week

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import uab.cs422.projectinlook.EventDao
import uab.cs422.projectinlook.EventDatabase
import uab.cs422.projectinlook.databinding.FragmentWeekBinding
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.CalendarInterface
import uab.cs422.projectinlook.ui.SwipeListener
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("DEPRECATION")
class WeekFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentWeekBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var dao: EventDao
    private val today = LocalDateTime.now()
    private var weekDays = (Array(7) { today }).toMutableList()
    private var dates: List<TextView> = listOf()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val weekRecyclerView = binding.weeklyRecycler
        dates = listOf(
            binding.date1,
            binding.date2,
            binding.date3,
            binding.date4,
            binding.date5,
            binding.date6,
            binding.date7
        )

        dao = EventDatabase.getInstance(this.requireContext()).eventDao
        setWeekDays()
        val events: MutableList<List<CalEvent>> = MutableList(7) { listOf() }
        runOnIO {
            for ((i, day) in weekDays.withIndex()) {
                events[i] = dao.getEventsOfDay(day.dayOfMonth, day.monthValue, day.year)
            }
        }
        weekRecyclerView.adapter = WeekDayAdapter(this, events, weekDays)
        weekRecyclerView.scrollToPosition(LocalDateTime.now().hour)

        weekRecyclerView.setOnTouchListener(@SuppressLint("ClickableViewAccessibility")
        object : SwipeListener(this@WeekFragment.context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                nextWeek()
                updateEvents()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                previousWeek()
                updateEvents()
            }
        })

        updateDisplayedDates()

        return root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (context as AppCompatActivity).supportActionBar?.title =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("LLLL"))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as AppCompatActivity).supportActionBar?.title =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("LLLL"))
    }

    override fun onResume() {
        super.onResume()
        updateEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun updateEvents() {
        val events: MutableList<List<CalEvent>> = MutableList(7) { listOf() }
        runOnIO {
            for ((i, day) in weekDays.withIndex()) {
                events[i] = dao.getEventsOfDay(day.dayOfMonth, day.monthValue, day.year)
            }
        }
        (binding.weeklyRecycler.adapter as WeekDayAdapter).updateWeekRecView(events)
    }

    override fun onTodayButtonClicked() {
        setWeekDays()
        updateDisplayedDates()
    }

    private fun setWeekDays() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        weekDays[0] = calendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        for (i in 1..6) {
            weekDays[i] = weekDays[0].plusDays(i.toLong())
        }
    }


    private fun previousWeek() {
        for (i in 0..6) {
            weekDays[i] = weekDays[i].minusDays(7)
        }
        updateDisplayedDates()
    }

    private fun nextWeek() {
        for (i in 0..6) {
            weekDays[i] = weekDays[i].plusDays(7)
        }
        updateDisplayedDates()
    }

    private fun updateDisplayedDates() {
        for ((count, date) in dates.withIndex()) {
            date.text = "${weekDays[count].dayOfMonth}"
        }

        // Set color of the day if it is today
        resetColors()
        if ((today.isAfter(weekDays[0]) || today.isEqual(weekDays[0])) &&
            (today.isBefore(weekDays[6]) || today.isEqual(weekDays[6]))
        ) {
            val typedValue = TypedValue()
            binding.weekDayDates.context.theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnTertiaryContainer,
                typedValue,
                true
            )
            val textColor = typedValue.data
            binding.weekDayDates.context.theme.resolveAttribute(
                com.google.android.material.R.attr.colorTertiaryContainer,
                typedValue,
                true
            )
            val backgroundColor = typedValue.data

            dates[today.dayOfWeek.value].setBackgroundColor(Color.valueOf(backgroundColor).toArgb())
            dates[today.dayOfWeek.value].setTextColor(Color.valueOf(textColor).toArgb())
        }
        determineMonth()

        updateEvents()
    }

    private fun resetColors() {
        val typedValue = TypedValue()
        binding.weekDayDates.context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnBackground,
            typedValue,
            true
        )
        for (date in dates) {
            date.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
            date.setTextColor(typedValue.data)
        }
    }

    private fun determineMonth() {
        print(today.dayOfWeek.value)

        (context as AppCompatActivity).supportActionBar?.title =
            weekDays[today.dayOfWeek.value].format(DateTimeFormatter.ofPattern("LLLL"))
    }
}