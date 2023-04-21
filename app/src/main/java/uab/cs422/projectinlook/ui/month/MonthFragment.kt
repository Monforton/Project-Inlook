package uab.cs422.projectinlook.ui.month

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.databinding.FragmentMonthBinding
import uab.cs422.projectinlook.ui.CalendarInterface
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

class MonthFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentMonthBinding? = null
    private val binding get() = _binding!!
    private lateinit var monthRecyclerView: RecyclerView
    private var selectedDate: LocalDate = LocalDate.now()
    private val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    private var dayViews: List<TextView> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthBinding.inflate(inflater, container, false)
        val root: View = binding.root
        monthRecyclerView = binding.monthRecyclerView
        dayViews = listOf(
            binding.monthWeekday1,
            binding.monthWeekday2,
            binding.monthWeekday3,
            binding.monthWeekday4,
            binding.monthWeekday5,
            binding.monthWeekday6,
            binding.monthWeekday7,
        )

        setMonthView()
        return root
    }

    private fun setMonthView() {
        (context as AppCompatActivity).supportActionBar?.title =
            selectedDate.format(formatter)
        val monthAdapter = MonthAdapter(this, selectedDate)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this.requireContext(), 7)
        monthRecyclerView.layoutManager = layoutManager
        monthRecyclerView.adapter = monthAdapter

        val prefFirstDay = when (PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString("first_day", "")) {
            "local" -> WeekFields.of(Locale.getDefault()).firstDayOfWeek.value
            "sat" -> DayOfWeek.SATURDAY.value
            "sun" -> DayOfWeek.SUNDAY.value
            "mon" -> DayOfWeek.MONDAY.value
            else -> WeekFields.of(Locale.getDefault()).firstDayOfWeek.value
        }

        for ((count, day) in dayViews.withIndex()) {
            day.text =
                DayOfWeek.of(((prefFirstDay + count - 1) % 7) + 1)
                    .getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }

        updateEvents()
    }

    override fun onResume() {
        super.onResume()
        setMonthView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (context as AppCompatActivity).supportActionBar?.title =
            LocalDate.now().format(formatter)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as AppCompatActivity).supportActionBar?.title =
            LocalDate.now().format(formatter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun updateEvents() {
        (binding.monthRecyclerView.adapter as MonthAdapter).updateEventRecycler()
    }

    override fun onTodayButtonClicked() {
        selectedDate = LocalDate.now()
    }

    override fun onSwipeLeft() {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }

    override fun onSwipeRight() {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
    }
}