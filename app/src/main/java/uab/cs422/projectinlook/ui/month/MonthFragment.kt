package uab.cs422.projectinlook.ui.month

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.databinding.FragmentMonthBinding
import uab.cs422.projectinlook.ui.CalendarInterface
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MonthFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentMonthBinding? = null
    private val binding get() = _binding!!
    private lateinit var monthRecyclerView: RecyclerView
    private var selectedDate: LocalDate = LocalDateTime.now().toLocalDate()
    private val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthBinding.inflate(inflater, container, false)
        val root: View = binding.root
        monthRecyclerView = binding.monthRecyclerView

        monthRecyclerView = binding.monthRecyclerView

        setMonthView()
        return root
    }

    private fun setMonthView() {
        (context as AppCompatActivity).supportActionBar?.title =
            selectedDate.format(formatter)
        val daysInMonth = daysInMonthArray(selectedDate)
        val monthAdapter = MonthAdapter(daysInMonth)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this.requireContext(), 7)
        monthRecyclerView.layoutManager = layoutManager
        monthRecyclerView.adapter = monthAdapter
        updateEvents()
    }

    private fun daysInMonthArray(date: LocalDate?): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (context as AppCompatActivity).supportActionBar?.title =
            LocalDateTime.now().format(formatter)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as AppCompatActivity).supportActionBar?.title =
            LocalDateTime.now().format(formatter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun updateEvents() {

    }

    override fun onTodayButtonClicked() {
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