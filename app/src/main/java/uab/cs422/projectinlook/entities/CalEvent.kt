package uab.cs422.projectinlook.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.graphics.Color
import androidx.core.graphics.toColor
import java.time.LocalDateTime

@Entity
data class CalEvent(
    @PrimaryKey(autoGenerate = true) var uid: Long,
    var startYear: Int,
    var startMonth: Int,
    var startDayOfMonth: Int,
    var startHour: Int,
    var startMinute: Int,
    var endYear: Int,
    var endMonth: Int,
    var endDay: Int,
    var endHour: Int,
    var endMinute: Int,
    var title: String,
    var desc: String,
    var colorA: Float,
    var colorR: Float,
    var colorG: Float,
    var colorB: Float
) {
    constructor( // Always use this constructor whenever creating an event
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        title: String,
        desc: String = "",
        color: Color = Color.RED.toColor()
    ) : this(
        0,
        startTime.year,
        startTime.monthValue,
        startTime.dayOfMonth,
        startTime.hour,
        startTime.minute,
        endTime.year,
        endTime.monthValue,
        endTime.dayOfMonth,
        endTime.hour,
        endTime.minute,
        title,
        desc,
        color.alpha(),
        color.red(),
        color.green(),
        color.blue()
    )

}