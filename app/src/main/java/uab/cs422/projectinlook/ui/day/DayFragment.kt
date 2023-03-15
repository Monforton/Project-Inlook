package uab.cs422.projectinlook.ui.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import uab.cs422.projectinlook.adapters.DayHourAdapter
import uab.cs422.projectinlook.databinding.FragmentDayBinding
import uab.cs422.projectinlook.entities.CalEvent

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
        val dayViewModel =
            ViewModelProvider(this).get(DayViewModel::class.java)

        _binding = FragmentDayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val hourRecyclerView = binding.hourRecycler
        hourRecyclerView.adapter =
            DayHourAdapter(
                listOf(
                    CalEvent(time = "8:00 AM", title = "event 1"),
                    CalEvent(time = "9:00 AM", title = "event 2")
                )
            )

//        val textView: TextView = binding.textDay
//        dayViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}