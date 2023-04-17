package uab.cs422.projectinlook.entities

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
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
    var endDayOfMonth: Int,
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
        color: Color
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

    fun getStartAsLocalDateTime(): LocalDateTime = LocalDateTime.of(startYear, startMonth, startDayOfMonth, startHour, startMinute)

    fun getEndAsLocalDateTime(): LocalDateTime = LocalDateTime.of(endYear, endMonth, endDayOfMonth, endHour, endMinute)

}