package uab.cs422.projectinlook.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.database.EventDao
import uab.cs422.projectinlook.database.EventDatabase
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.database.entities.CalEvent
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventsViewDialogFragment(val day: LocalDateTime, val displayMode: Int) : DialogFragment() {
    private lateinit var recycler: RecyclerView
    private lateinit var dao: EventDao

    companion object {
        const val HOUR_DISPLAY = 0
        const val DAY_DISPLAY = 1
    }

    override fun onStart() {
        super.onStart()

        // Set the width and height
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.6).toInt()
        dialog?.window?.setLayout(width, height)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_fragment_events_view, container, false)
        recycler = view.findViewById(R.id.dialog_events_recycler)

        view.findViewById<TextView>(R.id.dialog_events_title).text = day.format(
            DateTimeFormatter.ofPattern("d E")
        )

        dao = EventDatabase.getInstance(this.requireContext()).eventDao
        var events: MutableList<CalEvent> = mutableListOf()
        runOnIO {
            events = dao.getEventsOfDay(day.dayOfMonth, day.monthValue, day.year).toMutableList()
            if (displayMode == HOUR_DISPLAY) {
                val size = events.size
                for (i in 0 until size - 1) {
                    val start = events[i].getStartAsLocalDateTime()
                    val end = events[i].getEndAsLocalDateTime()
                    if (!((start.isBefore(day) || start.isEqual(day)) &&
                                (end.isAfter(day) || end.isEqual(day)))
                    ) {
                        events.remove(events[i])
                    }
                }
            }
        }

        recycler.adapter = EventsViewEventsAdapter(this, events, day)

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawableResource(R.drawable.background_rounded_corners_large)

        return dialog
    }
}