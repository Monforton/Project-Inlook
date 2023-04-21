package uab.cs422.projectinlook.database.entities

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat()
    ) {
    }

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

    fun getStartAsLocalDateTime(): LocalDateTime =
        LocalDateTime.of(startYear, startMonth, startDayOfMonth, startHour, startMinute)

    fun getEndAsLocalDateTime(): LocalDateTime =
        LocalDateTime.of(endYear, endMonth, endDayOfMonth, endHour, endMinute)

    fun getColorAsColor(): Color = Color.valueOf(colorR, colorG, colorB, colorA)

    fun reconstruct(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        title: String,
        desc: String,
        color: Color
    ) {
        startYear = startTime.year
        startMonth = startTime.monthValue
        startDayOfMonth = startTime.dayOfMonth
        startHour = startTime.hour
        startMinute = startTime.minute
        endYear = endTime.year
        endMonth = endTime.monthValue
        endDayOfMonth = endTime.dayOfMonth
        endHour = endTime.hour
        endMinute = endTime.minute
        this.title = title
        this.desc = desc
        colorA = color.alpha()
        colorR = color.red()
        colorG = color.green()
        colorB = color.blue()

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(uid)
        parcel.writeInt(startYear)
        parcel.writeInt(startMonth)
        parcel.writeInt(startDayOfMonth)
        parcel.writeInt(startHour)
        parcel.writeInt(startMinute)
        parcel.writeInt(endYear)
        parcel.writeInt(endMonth)
        parcel.writeInt(endDayOfMonth)
        parcel.writeInt(endHour)
        parcel.writeInt(endMinute)
        parcel.writeString(title)
        parcel.writeString(desc)
        parcel.writeFloat(colorA)
        parcel.writeFloat(colorR)
        parcel.writeFloat(colorG)
        parcel.writeFloat(colorB)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CalEvent> {
        override fun createFromParcel(parcel: Parcel): CalEvent {
            return CalEvent(parcel)
        }

        override fun newArray(size: Int): Array<CalEvent?> {
            return arrayOfNulls(size)
        }
    }

}