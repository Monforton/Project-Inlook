package uab.cs422.projectinlook.entities

import android.graphics.Color
import androidx.core.graphics.toColor
import java.time.LocalDateTime

data class CalEvent(
    var time: LocalDateTime,
    var title: String,
    var desc: String = "",
    var color: Color = Color.RED.toColor()
)