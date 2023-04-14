package uab.cs422.projectinlook.ui.week

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import java.util.Calendar
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import uab.cs422.projectinlook.EventDao
import uab.cs422.projectinlook.EventDatabase
import uab.cs422.projectinlook.databinding.FragmentWeekBinding
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.CalendarInterface
import uab.cs422.projectinlook.ui.SwipeListener
import uab.cs422.projectinlook.util.runOnIO
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class WeekFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentWeekBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dao: EventDao
    private val today = LocalDateTime.now()
    private var weekDays = (Array(7) { today }).toMutableList()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val weekRecyclerView = binding.weeklyRecycler

        dao = EventDatabase.getInstance(this.requireContext()).eventDao
        var events: List<CalEvent>
        runOnIO {
            events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)

            weekRecyclerView.adapter = WeekEventAdapter(this, events)
        }


        getCurrentWeek()


        weekRecyclerView.setOnTouchListener( @SuppressLint("ClickableViewAccessibility")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun updateEvents() {
        var events: List<CalEvent> = listOf()
        runOnIO {
            events = dao.getEventsInRange(
                weekDays[0].dayOfMonth,
                weekDays[0].monthValue,
                weekDays[0].year,
                weekDays[6].dayOfMonth,
                weekDays[6].monthValue,
                weekDays[6].year
            )
        }
        (binding.weeklyRecycler.adapter as WeekEventAdapter).updateWeekRecView(events)
    }

    override fun onTodayButtonClicked() {
        getCurrentWeek()
    }

    private fun getCurrentWeek() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        weekDays[0] = calendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        for (i in 1..6) {
            weekDays[i] = weekDays[0].plusDays(i.toLong())
        }
        updateDisplayedDates()
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
        binding.date1.text = "${weekDays[0].dayOfMonth}"
        binding.date2.text = "${weekDays[1].dayOfMonth}"
        binding.date3.text = "${weekDays[2].dayOfMonth}"
        binding.date4.text = "${weekDays[3].dayOfMonth}"
        binding.date5.text = "${weekDays[4].dayOfMonth}"
        binding.date6.text = "${weekDays[5].dayOfMonth}"
        binding.date7.text = "${weekDays[6].dayOfMonth}"

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
            when (today.dayOfWeek) {
                DayOfWeek.SUNDAY -> {
                    binding.date1.setBackgroundColor(Color.valueOf(backgroundColor).toArgb())
                    binding.date1.setTextColor(Color.valueOf(textColor).toArgb())
                }
                DayOfWeek.MONDAY -> {
                    binding.date2.setBackgroundColor(Color.valueOf(backgroundColor).toArgb())
                    binding.date2.setTextColor(Color.valueOf(textColor).toArgb())
                }
                DayOfWeek.TUESDAY -> {
                    binding.date3.setBackgroundColor(Color.valueOf(backgroundColor).toArgb())
                    binding.date3.setTextColor(Color.valueOf(textColor).toArgb())
                }
                DayOfWeek.WEDNESDAY -> {
                    binding.date4.setBackgroundColor(Color.valueOf(backgroundColor).toArgb())
                    binding.date4.setTextColor(Color.valueOf(textColor).toArgb())
                }
                DayOfWeek.THURSDAY -> {
                    binding.date5.setBackgroundColor(Color.valueOf(backgroundColor).toArgb())
                    binding.date5.setTextColor(Color.valueOf(textColor).toArgb())
                }
                DayOfWeek.FRIDAY -> {
                    binding.date6.setBackgroundColor(Color.valueOf(backgroundColor).toArgb())
                    binding.date6.setTextColor(Color.valueOf(textColor).toArgb())
                }
                DayOfWeek.SATURDAY -> {
                    binding.date7.setBackgroundColor(Color.valueOf(backgroundColor).toArgb())
                    binding.date7.setTextColor(Color.valueOf(textColor).toArgb())
                }
                else -> {}
            }
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
        binding.date1.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
        binding.date2.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
        binding.date3.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
        binding.date4.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
        binding.date5.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
        binding.date6.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())
        binding.date7.setBackgroundColor(Color.valueOf(0f, 0f, 0f, 0f).toArgb())

        binding.date1.setTextColor(typedValue.data)
        binding.date2.setTextColor(typedValue.data)
        binding.date3.setTextColor(typedValue.data)
        binding.date4.setTextColor(typedValue.data)
        binding.date5.setTextColor(typedValue.data)
        binding.date6.setTextColor(typedValue.data)
        binding.date7.setTextColor(typedValue.data)
    }

    private fun determineMonth() {
        print(today.dayOfWeek.value)

        (context as AppCompatActivity).supportActionBar?.title =
            weekDays[today.dayOfWeek.value].format(DateTimeFormatter.ofPattern("LLLL"))
    }
}