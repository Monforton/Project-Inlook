package uab.cs422.projectinlook.ui.week

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import uab.cs422.projectinlook.EventDao
import uab.cs422.projectinlook.EventDatabase
import uab.cs422.projectinlook.databinding.FragmentWeekBinding
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.CalendarInterface
import uab.cs422.projectinlook.ui.day.DayHourAdapter
import uab.cs422.projectinlook.util.runOnIO
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class WeekFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentWeekBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dao: EventDao
    private val today = LocalDateTime.now()
    private lateinit var day1: LocalDateTime
    private lateinit var day2: LocalDateTime
    private lateinit var day3: LocalDateTime
    private lateinit var day4: LocalDateTime
    private lateinit var day5: LocalDateTime
    private lateinit var day6: LocalDateTime
    private lateinit var day7: LocalDateTime

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val weekViewModel =
            ViewModelProvider(this).get(WeekViewModel::class.java)

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

        binding.previousWeekBtn.setOnClickListener {
        previousWeek()
        }

        binding.todayBtn.setOnClickListener {
        getCurrentWeek()
        }

        binding.nextWeekBtn.setOnClickListener {
        nextWeek()
        }

        binding.date1.setOnClickListener {

        }

        binding.date2.setOnClickListener {

        }

        binding.date3.setOnClickListener {

        }

        binding.date4.setOnClickListener {

        }

        binding.date5.setOnClickListener {

        }

        binding.date6.setOnClickListener {

        }

        binding.date7.setOnClickListener {

        }

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

    }

    private fun getCurrentWeek() {
        if (today.dayOfWeek == DayOfWeek.valueOf("SUNDAY")) {
            day1 = today
            binding.date1.setBackgroundColor(Color.parseColor("#C84EBD72"))
            day2 = today.plusDays(1)
            day3 = today.plusDays(2)
            day4 = today.plusDays(3)
            day5 = today.plusDays(4)
            day6 = today.plusDays(5)
            day7 = today.plusDays(6)
            updateDisplayedDates()
        }
        else if (today.dayOfWeek == DayOfWeek.valueOf("MONDAY")) {
            day1 = today.minusDays(1)
            day2 = today
            binding.date2.setBackgroundColor(Color.parseColor("#C84EBD72"))
            day3 = today.plusDays(1)
            day4 = today.plusDays(2)
            day5 = today.plusDays(3)
            day6 = today.plusDays(4)
            day7 = today.plusDays(5)
            updateDisplayedDates()
        }

        else if (today.dayOfWeek == DayOfWeek.valueOf("TUESDAY")) {
            day1 = today.minusDays(2)
            day2 = today.minusDays(1)
            day3 = today
            binding.date3.setBackgroundColor(Color.parseColor("#C84EBD72"))
            day4 = today.plusDays(1)
            day5 = today.plusDays(2)
            day6 = today.plusDays(3)
            day7 = today.plusDays(4)
            updateDisplayedDates()
        }

        else if (today.dayOfWeek == DayOfWeek.valueOf("WEDNESDAY")) {
            day1 = today.minusDays(3)
            day2 = today.minusDays(2)
            day3 = today.minusDays(1)
            day4 = today
            binding.date4.setBackgroundColor(Color.parseColor("#C84EBD72"))
            day5 = today.plusDays(1)
            day6 = today.plusDays(2)
            day7 = today.plusDays(3)
            updateDisplayedDates()
        }

        else if (today.dayOfWeek == DayOfWeek.valueOf("THURSDAY")) {
            day1 = today.minusDays(4)
            day2 = today.minusDays(3)
            day3 = today.minusDays(2)
            day4 = today.minusDays(1)
            day5 = today
            binding.date5.setBackgroundColor(Color.parseColor("#C84EBD72"))
            day6 = today.plusDays(1)
            day7 = today.plusDays(2)
            updateDisplayedDates()
        }

        else if (today.dayOfWeek == DayOfWeek.valueOf("FRIDAY")) {
            day1 = today.minusDays(5)
            day2 = today.minusDays(4)
            day3 = today.minusDays(3)
            day4 = today.minusDays(2)
            day5 = today.minusDays(1)
            day6 = today
            binding.date6.setBackgroundColor(Color.parseColor("#C84EBD72"))
            day7 = today.plusDays(1)
            updateDisplayedDates()
        }

        else if (today.dayOfWeek == DayOfWeek.valueOf("SATURDAY")) {
            day1 = today.minusDays(6)
            day2 = today.minusDays(5)
            day3 = today.minusDays(4)
            day4 = today.minusDays(3)
            day5 = today.minusDays(2)
            day6 = today.minusDays(1)
            day7 = today
            binding.date7.setBackgroundColor(Color.parseColor("#C84EBD72"))
            updateDisplayedDates()
        }
    }
    private fun previousWeek() {
        day1 = day1.minusDays(7)
        day2 = day2.minusDays(7)
        day3 = day3.minusDays(7)
        day4 = day4.minusDays(7)
        day5 = day5.minusDays(7)
        day6 = day6.minusDays(7)
        day7 = day7.minusDays(7)
        updateDisplayedDates()
    }

    private fun nextWeek() {
        day1 = day1.plusDays(7)
        day2 = day2.plusDays(7)
        day3 = day3.plusDays(7)
        day4 = day4.plusDays(7)
        day5 = day5.plusDays(7)
        day6 = day6.plusDays(7)
        day7 = day7.plusDays(7)
        updateDisplayedDates()
    }

    private fun updateDisplayedDates() {
        binding.date1.text = "${day1.dayOfMonth}"
        binding.date2.text = "${day2.dayOfMonth}"
        binding.date3.text = "${day3.dayOfMonth}"
        binding.date4.text = "${day4.dayOfMonth}"
        binding.date5.text = "${day5.dayOfMonth}"
        binding.date6.text = "${day6.dayOfMonth}"
        binding.date7.text = "${day7.dayOfMonth}"
        binding.date1.setBackgroundColor(Color.parseColor("#00000000"))
        determineMonth()
    }

    private fun resetDateBackgroundColors() {
        binding.date1.setBackgroundColor(Color.parseColor("#00000000"))
        binding.date2.setBackgroundColor(Color.parseColor("#00000000"))
        binding.date3.setBackgroundColor(Color.parseColor("#00000000"))
        binding.date4.setBackgroundColor(Color.parseColor("#00000000"))
        binding.date5.setBackgroundColor(Color.parseColor("#00000000"))
        binding.date6.setBackgroundColor(Color.parseColor("#00000000"))
        binding.date7.setBackgroundColor(Color.parseColor("#00000000"))
    }

    private fun determineMonth() {
        if (day1.month == day7.month) binding.currentMonth.text = "${day7.month}"
        else {
            if (day1.month != day2.month) binding.currentMonth.text = "${day2.month}"
            else if (day1.month != day3.month) binding.currentMonth.text = "${day3.month}"
            else if (day1.month != day4.month) binding.currentMonth.text = "${day4.month}"
            else {
                binding.currentMonth.text = "${day1.month}"
            }
        }
    }
}