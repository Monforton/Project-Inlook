package uab.cs422.projectinlook.ui.today

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import uab.cs422.projectinlook.database.EventDao
import uab.cs422.projectinlook.database.EventDatabase
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.databinding.FragmentTodayBinding
import uab.cs422.projectinlook.database.entities.CalEvent
import uab.cs422.projectinlook.ui.CalendarInterface
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime

class TodayFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!
    lateinit var dao: EventDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the adapter
        val eventsRecyclerView = binding.todayEventsRecycler
        dao = EventDatabase.getInstance(this.requireContext()).eventDao
        var events: List<CalEvent> = listOf()
        val today = LocalDateTime.now()
        runOnIO {
            events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)
        }
        eventsRecyclerView.adapter = TodayEventsAdapter(this, events)

        binding.noEventsTV.alpha = if (events.isNotEmpty()) 0f else 1f

        return root
    }

    // Usually called after returning from AddEvent or from Settings,
    // updates the data shown
    override fun onResume() {
        super.onResume()
        val today = LocalDateTime.now()
        var events:List<CalEvent> = listOf()
        runOnIO {
            events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)
        }
        binding.todayEventsRecycler.adapter = TodayEventsAdapter(this, events)
    }

    // Set the Toolbar's text
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (context as AppCompatActivity).supportActionBar?.title = getString(R.string.title_today)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context as AppCompatActivity).supportActionBar?.title = getString(R.string.title_today)
    }

    // Updates shown events in the RecyclerView
    override fun updateEvents() {
        var events: List<CalEvent> = listOf()
        val today = LocalDateTime.now()
        runOnIO {
            events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)
        }
        (binding.todayEventsRecycler.adapter as TodayEventsAdapter).updateDisplayedData(events)
        binding.noEventsTV.alpha = if (events.isNotEmpty()) 0f else 1f

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Unused Interface functions
    override fun onTodayButtonClicked() {    }
    override fun onSwipeLeft() {    }
    override fun onSwipeRight() {    }
}