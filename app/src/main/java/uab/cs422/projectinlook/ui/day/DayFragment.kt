package uab.cs422.projectinlook.ui.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uab.cs422.projectinlook.databinding.FragmentDayBinding
import uab.cs422.projectinlook.entities.CalEvent
import java.time.LocalDateTime

class DayFragment : Fragment() {

    private var _binding: FragmentDayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val dayViewModel =
//            ViewModelProvider(this)[DayViewModel::class.java]

        _binding = FragmentDayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val hourRecyclerView = binding.hourRecycler
        hourRecyclerView.adapter =
            DayHourAdapter(
                listOf(
                    CalEvent(time = LocalDateTime.of(2023, 3, 16, 8, 0),  title = "event 1"),
                    CalEvent(time = LocalDateTime.of(2023, 3, 16, 8, 0),  title = "event 2"),
                    CalEvent(time = LocalDateTime.of(2023, 3, 16, 9, 0),  title = "event 2"),
                    CalEvent(time = LocalDateTime.of(2023, 3, 16, 10, 0), title = "event 3")
                )
            )
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}