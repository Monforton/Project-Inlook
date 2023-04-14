package uab.cs422.projectinlook.ui.today

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import uab.cs422.projectinlook.EventDao
import uab.cs422.projectinlook.EventDatabase
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.databinding.FragmentTodayBinding
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.ui.CalendarInterface
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime

class TodayFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentTodayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var dao: EventDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val eventsRecyclerView = binding.eventsRecycler

        dao = EventDatabase.getInstance(this.requireContext()).eventDao
        var events: List<CalEvent>
        val today = LocalDateTime.now()
        runOnIO {
            events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)
            eventsRecyclerView.adapter = TodayEventsAdapter(events)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        val today = LocalDateTime.now()
        var events:List<CalEvent> = listOf()
        runOnIO {
            events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)
        }
        binding.eventsRecycler.adapter = TodayEventsAdapter(events)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (context as AppCompatActivity).supportActionBar?.title = getString(R.string.title_today)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as AppCompatActivity).supportActionBar?.title = getString(R.string.title_today)
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
        (binding.eventsRecycler.adapter as TodayEventsAdapter).updateDisplayedData(events)
    }

    override fun onTodayButtonClicked() {    }

    override fun onSwipeLeft() {    }

    override fun onSwipeRight() {    }
}