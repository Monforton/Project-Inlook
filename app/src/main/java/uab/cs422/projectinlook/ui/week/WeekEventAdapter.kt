package uab.cs422.projectinlook.ui.week

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent

class WeekEventAdapter(private val fragment: WeekFragment, private var eventData: List<CalEvent>) :
    RecyclerView.Adapter<WeekEventAdapter.ViewHolder>() {
    private val data = eventData.toMutableList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayEventTitleView: TextView = view.findViewById(R.id.dayEventTitle)
        val dailyEventLayout: LinearLayout = view.findViewById(R.id.dailyEventDetails)
        val dailyEventDate: TextView = view.findViewById(R.id.dailyEventDate)
        val dailyEventDetails: TextView = view.findViewById(R.id.dailyEventDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.cell_daily, parent, false)
    )

    override fun getItemCount() = 24

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = eventData[position]
        holder.dayEventTitleView.text = event.title
        val eventDate = event.startMonth.toString() + " " +event.startDayOfMonth.toString() + " " + event.startYear.toString()
        holder.dailyEventDate.text = eventDate
        holder.dailyEventDetails.text = event.desc
    }

    fun updateWeekRecView(newData: List<CalEvent>) {
        data.addAll(newData)
        notifyDataSetChanged()
    }
}