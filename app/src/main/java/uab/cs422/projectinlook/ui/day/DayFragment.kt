package uab.cs422.projectinlook.ui.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uab.cs422.projectinlook.EventDao
import uab.cs422.projectinlook.EventDatabase
import uab.cs422.projectinlook.databinding.FragmentDayBinding
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime

class DayFragment : Fragment() {

    private var _binding: FragmentDayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dao: EventDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val hourRecyclerView = binding.hourRecycler

        // Set up adapter
        dao = EventDatabase.getInstance(this.requireContext()).eventDao
        var events: List<CalEvent> = listOf()
        val today = LocalDateTime.now()
        runOnIO {
            events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)
//          Debug command, just uncomment block to add events
            /*
            dao.insertEvent(
            CalEvent(
            startTime = LocalDateTime.of(2023, 3, 16, 7, 0),
            endTime = LocalDateTime.of(2023, 3, 16, 7, 0),
            title = "event 1"
            ),
            CalEvent(
            startTime = LocalDateTime.of(2023, 3, 16, 8, 0),
            endTime = LocalDateTime.of(2023, 3, 16, 9, 0),
            title = "event 2"
            ),
            CalEvent(
            startTime = LocalDateTime.of(2023, 3, 16, 8, 0),
            endTime = LocalDateTime.of(2023, 3, 16, 9, 0),
            title = "event 2"
            ),
            CalEvent(
            startTime = LocalDateTime.of(2023, 3, 16, 8, 0),
            endTime = LocalDateTime.of(2023, 3, 16, 9, 0),
            title = "event 2"
            ),
            CalEvent(
            startTime = LocalDateTime.of(2023, 3, 16, 8, 0),
            endTime = LocalDateTime.of(2023, 3, 16, 9, 0),
            title = "event 2"
            ),
            CalEvent(
            startTime = LocalDateTime.of(2023, 3, 16, 9, 0),
            endTime = LocalDateTime.of(2023, 3, 16, 10, 0),
            title = "event 3"
            ),
            CalEvent(
            startTime = LocalDateTime.of(2023, 3, 16, 10, 0),
            endTime = LocalDateTime.of(2023, 3, 16, 11, 0),
            title = "event 4"
            )
            )
            */
            hourRecyclerView.adapter = DayHourAdapter(events)

            binding.hourRecycler.scrollToPosition(LocalDateTime.now().hour)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}