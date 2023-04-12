package uab.cs422.projectinlook.ui.day

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
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DayFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentDayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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
        var events: List<CalEvent>
        val today = LocalDateTime.now()
        runOnIO {
            events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)

            hourRecyclerView.adapter = DayHourAdapter(this, events)

            binding.hourRecycler.scrollToPosition(LocalDateTime.now().hour)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        val today = LocalDateTime.now()
        runOnIO {
            val events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)

            binding.hourRecycler.adapter = DayHourAdapter(this, events)

            binding.hourRecycler.scrollToPosition(LocalDateTime.now().hour)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (context as AppCompatActivity).supportActionBar?.title =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("LLLL d"))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as AppCompatActivity).supportActionBar?.title =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("LLLL d"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun updateEvents() {
        var events: List<CalEvent> = listOf()
        val today = LocalDateTime.now()
        runOnIO {
            events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)
        }
        (binding.hourRecycler.adapter as DayHourAdapter).updateDisplayedData(events)
    }

    override fun onTodayButtonClicked() {

    }
}