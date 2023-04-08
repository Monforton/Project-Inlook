package uab.cs422.projectinlook.ui.week

import android.content.Context
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Suppress("DEPRECATION")
class WeekFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentWeekBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var dao: EventDao

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
        val today = LocalDateTime.now()
        runOnIO {
            events = dao.getEventsOfDay(today.dayOfMonth, today.monthValue, today.year)

            weekRecyclerView.adapter = WeekEventAdapter(this, events)
        }

        //val textView: TextView = binding.weekView
        //weekViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}
        binding.previousWeekBtn.setOnClickListener {

        }

        binding.todayBtn.setOnClickListener {

        }

        binding.nextWeekBtn.setOnClickListener {

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
}