package uab.cs422.projectinlook.adapters

import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import uab.cs422.projectinlook.R
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.util.ScreenConversion

class DayHourAdapter(val eventData: List<CalEvent>): RecyclerView.Adapter<DayHourAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val hourTextView = view.findViewById<TextView>(R.id.hourText)
        val eventsLayout = view.findViewById<LinearLayout>(R.id.eventsLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.cell_hour, parent, false)
    )

    override fun getItemCount() = 2

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.hourTextView.text = eventData[position].time
        for (i in eventData) {
            val eventTextView = TextView(holder.eventsLayout.context)
            eventTextView.text = i.title
            eventTextView.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
            eventTextView.background = AppCompatResources.getDrawable(eventTextView.context,
                R.drawable.event_back
            )
            eventTextView.ellipsize = TextUtils.TruncateAt.END
            eventTextView.gravity = Gravity.CENTER
            eventTextView.maxLines = 1
            eventTextView.setPaddingRelative(
                ScreenConversion.dpToPx(eventTextView.context, 3),
                0,
                0,
                ScreenConversion.dpToPx(eventTextView.context, 2))
            eventTextView.setTextColor(Color.BLACK)
            eventTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            eventTextView.typeface = Typeface.DEFAULT_BOLD
            eventTextView.setOnClickListener {
                Snackbar.make(it, "Clicked on event", 5000).show()
            }
            holder.eventsLayout.addView(eventTextView)
        }
    }

}