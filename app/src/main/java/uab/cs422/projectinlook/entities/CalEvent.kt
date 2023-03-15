package uab.cs422.projectinlook.entities

import android.graphics.Color
import androidx.core.graphics.toColor

data class CalEvent(
    var time: String,
    var title: String,
    var desc: String = "",
    var color: Color = Color.RED.toColor()
)