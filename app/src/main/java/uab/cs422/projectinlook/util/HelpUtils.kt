package uab.cs422.projectinlook.util

import android.content.Context
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uab.cs422.projectinlook.entities.CalEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Used for functions dealing with the database
 */
fun runOnIO(lambda: suspend () -> Unit) {
    runBlocking {
        launch(Dispatchers.IO) {
            lambda()
        }
    }
}

/**
 * Converts dp to px
 */
fun dpToPx(context: Context, dp: Int): Int {
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

/**
 * Given CalEvent, returns it as a LocalDateTime variable
 */
fun getCalEventAsLocalDateTime(endTime: Boolean = false, event: CalEvent): LocalDateTime {
    return if (!endTime) {
        LocalDateTime.of(
            event.startYear,
            event.startMonth,
            event.startDayOfMonth,
            event.startHour,
            event.startMinute
        )
    } else {
        LocalDateTime.of(
            event.endYear,
            event.endMonth,
            event.endDayOfMonth,
            event.endHour,
            event.endMinute
        )
    }
}

/**
 * The correct hour formatter for the user's chosen timestyle, 12Hr or 24Hr
 */
fun hourFormatter(context: Context): DateTimeFormatter =
    when (PreferenceManager.getDefaultSharedPreferences(context)
        .getString("hour_format", "")) {
        "clock_time_format" -> DateTimeFormatter.ofPattern("h a")
        "day_time_format" -> DateTimeFormatter.ofPattern("H:mm")
        else -> DateTimeFormatter.ofPattern("h a")
    }

/**
 * Returns an int as an hour of today
 */
fun intAsHour(date: LocalDate = LocalDateTime.now().toLocalDate(), hour: Int) =
    LocalDateTime.of(date, LocalTime.of(hour, 0))