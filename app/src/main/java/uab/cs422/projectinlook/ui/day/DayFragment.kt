package uab.cs422.projectinlook.ui.day

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import uab.cs422.projectinlook.EventDao
import uab.cs422.projectinlook.EventDatabase
import uab.cs422.projectinlook.MainActivity
import uab.cs422.projectinlook.databinding.FragmentDayBinding
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.CalendarInterface
import uab.cs422.projectinlook.ui.SwipeListener
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
@SuppressLint("ClickableViewAccessibility")
class DayFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!
    lateinit var dao: EventDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up adapter
        val hourRecyclerView = binding.hourRecycler
        dao = EventDatabase.getInstance(this.requireContext()).eventDao
        var events: List<CalEvent> = listOf()
        val day = (activity as MainActivity).selectedDay
        runOnIO {
            events = dao.getEventsOfDay(day.dayOfMonth, day.monthValue, day.year)
        }
        hourRecyclerView.adapter = DayHourAdapter(this, events, day)
        hourRecyclerView.scrollToPosition(LocalDateTime.now().hour)

        // Set the gesture detection
        hourRecyclerView.setOnTouchListener(
            object : SwipeListener(this@DayFragment.requireContext()) {
                override fun onSwipeLeft() {
                    super.onSwipeLeft()
                    this@DayFragment.onSwipeLeft()
                }

                override fun onSwipeRight() {
                    super.onSwipeRight()
                    this@DayFragment.onSwipeRight()
                }
            })
        return root
    }


    override fun onResume() {
        super.onResume()

        updateEvents()
    }

    // Set the toolbar text to the day's date
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (context as AppCompatActivity).supportActionBar?.title =
            (activity as MainActivity).selectedDay.format(DateTimeFormatter.ofPattern("LLLL d"))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as AppCompatActivity).supportActionBar?.title =
            (activity as MainActivity).selectedDay.format(DateTimeFormatter.ofPattern("LLLL d"))
    }

    // Update the adapter, title, and event data
    override fun updateEvents() {
        var events: List<CalEvent> = listOf()
        val day = (activity as MainActivity).selectedDay
        runOnIO {
            events = dao.getEventsOfDay(day.dayOfMonth, day.monthValue, day.year)
        }
        (binding.hourRecycler.adapter as DayHourAdapter).updateDisplayedData(events, day)

        (context as AppCompatActivity).supportActionBar?.title =
            day.format(DateTimeFormatter.ofPattern("LLLL d"))
    }

    // Action bindings
    override fun onTodayButtonClicked() {
        (activity as MainActivity).selectedDay = LocalDateTime.now()
        updateEvents()
    }

    override fun onSwipeLeft() {
        (activity as MainActivity).selectedDay =
            (activity as MainActivity).selectedDay.plusDays(1)
        updateEvents()
    }

    override fun onSwipeRight() {
        (activity as MainActivity).selectedDay =
            (activity as MainActivity).selectedDay.minusDays(1)
        updateEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}