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
import uab.cs422.projectinlook.databinding.FragmentDayBinding
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.CalendarInterface
import uab.cs422.projectinlook.ui.SwipeListener
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class DayFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentDayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var day = LocalDateTime.now()
    lateinit var dao: EventDao

    @SuppressLint("ClickableViewAccessibility")
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
        day = LocalDateTime.now()
        runOnIO {
            events = dao.getEventsOfDay(day.dayOfMonth, day.monthValue, day.year)
        }
        hourRecyclerView.adapter = DayHourAdapter(this, events)
        hourRecyclerView.scrollToPosition(LocalDateTime.now().hour)
        hourRecyclerView.setOnTouchListener(@SuppressLint("ClickableViewAccessibility")
        object : SwipeListener(this@DayFragment.context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                day = day.plusDays(1)
                updateEvents()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                day = day.minusDays(1)
                updateEvents()
            }
        })
        return root
    }


    override fun onResume() {
        super.onResume()

        updateEvents()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (context as AppCompatActivity).supportActionBar?.title =
            day.format(DateTimeFormatter.ofPattern("LLLL d"))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as AppCompatActivity).supportActionBar?.title =
            day.format(DateTimeFormatter.ofPattern("LLLL d"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun updateEvents() {
        var events: List<CalEvent> = listOf()
        runOnIO {
            events = dao.getEventsOfDay(day.dayOfMonth, day.monthValue, day.year)
        }
        (binding.hourRecycler.adapter as DayHourAdapter).updateDisplayedData(events)

        (context as AppCompatActivity).supportActionBar?.title =
            day.format(DateTimeFormatter.ofPattern("LLLL d"))
    }

    override fun onTodayButtonClicked() {
        day = LocalDateTime.now()
        updateEvents()
    }
}