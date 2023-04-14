package uab.cs422.projectinlook.ui.month

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import uab.cs422.projectinlook.databinding.FragmentMonthBinding
import uab.cs422.projectinlook.ui.CalendarInterface
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MonthFragment : Fragment(), CalendarInterface {

    private var _binding: FragmentMonthBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val monthViewModel =
            ViewModelProvider(this).get(MonthViewModel::class.java)

        _binding = FragmentMonthBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMonth
        monthViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
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

    override fun onTodayButtonClicked() {
    }

    override fun onSwipeLeft() {
    }

    override fun onSwipeRight() {
    }
}