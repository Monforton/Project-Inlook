package uab.cs422.projectinlook

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import uab.cs422.projectinlook.databinding.ActivityAddEventBinding
import uab.cs422.projectinlook.entities.CalEvent
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class AddEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEventBinding
    private lateinit var dao: EventDao
    private lateinit var addEventViewModel: AddEventViewModel
    private lateinit var longerFormat: DateTimeFormatter
    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    private var startDateTime: LocalDateTime = LocalDateTime.now().withMinute(0).plusHours(1)
        set(value) {
            field = value
            addEventViewModel.setStartTimeText(field.format(longerFormat))
            addEventViewModel.setStartDateText(buildString {
                append(startDateTime.format(DateTimeFormatter.ofPattern("E, MMM dd")))
                append("\n")
                append(startDateTime.format(DateTimeFormatter.ofPattern("yyyy")))
            })
        }
    private var endDateTime: LocalDateTime = startDateTime.plusHours(1)
        set(value) {
            field = value
            addEventViewModel.setEndTimeText(field.format(longerFormat))
            addEventViewModel.setEndDateText(buildString {
                append(endDateTime.format(DateTimeFormatter.ofPattern("E, MMM dd")))
                append("\n")
                append(endDateTime.format(DateTimeFormatter.ofPattern("yyyy")))
            })
        }
    private var color: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        longerFormat = when (PreferenceManager.getDefaultSharedPreferences(this)
            .getString("hour_format", "")) {
            "clock_time_format" -> DateTimeFormatter.ofPattern("hh:mm a")
            "day_time_format" -> DateTimeFormatter.ofPattern("HH:mm")
            else -> DateTimeFormatter.ofPattern("hh:mm a")
        }

        addEventViewModel = ViewModelProvider(this)[AddEventViewModel::class.java]
        addEventViewModel.startTimeText.observe(this) {
            binding.addStartTimeTextView.text = it
        }
        addEventViewModel.startDateText.observe(this) {
            binding.addStartDateTextView.text = it
        }
        addEventViewModel.endTimeText.observe(this) {
            binding.addEndTimeTextView.text = it
        }
        addEventViewModel.endDateText.observe(this) {
            binding.addEndDateTextView.text = it
        }
        startDateTime = LocalDateTime.now().withMinute(0)
        endDateTime = startDateTime.plusHours(1)

        dao = EventDatabase.getInstance(this).eventDao

        binding.addStartTimeTextView.setOnClickListener {
            val timePicker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(
                        when (PreferenceManager.getDefaultSharedPreferences(this)
                            .getString("hour_format", "")) {
                            "clock_time_format" -> TimeFormat.CLOCK_12H
                            "day_time_format" -> TimeFormat.CLOCK_24H
                            else -> TimeFormat.CLOCK_12H
                        }
                    )
                    .setHour(startDateTime.hour)
                    .setMinute(0)
                    .setTitleText("Select starting time")
                    .setInputMode(INPUT_MODE_CLOCK)
                    .build()
            timePicker.addOnPositiveButtonClickListener {
                startDateTime =
                    startDateTime.withHour(timePicker.hour).withMinute(timePicker.minute)
                if (startDateTime.isAfter(endDateTime)) {
                    endDateTime = startDateTime.plusHours(1)
                }
            }
            timePicker.show(supportFragmentManager, "timePicker")
        }

        binding.addStartDateTextView.setOnClickListener {
            calendar.set(
                startDateTime.year,
                startDateTime.month.value - 1,
                startDateTime.dayOfMonth
            )
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select start date")
                    .setSelection(calendar.timeInMillis)
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                startDateTime = LocalDateTime.ofEpochSecond(
                    (datePicker.selection!! / 1000),
                    0,
                    ZoneOffset.UTC
                ).withHour(startDateTime.hour).withMinute(startDateTime.minute)

                if (startDateTime.isAfter(endDateTime)) {
                    endDateTime =
                        startDateTime.withHour(endDateTime.hour).withMinute(endDateTime.minute)
                }
            }

            datePicker.show(supportFragmentManager, "datePicker")
        }

        binding.addEndTimeTextView.setOnClickListener {
            val timePicker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(
                        when (PreferenceManager.getDefaultSharedPreferences(this)
                            .getString("hour_format", "")) {
                            "clock_time_format" -> TimeFormat.CLOCK_12H
                            "day_time_format" -> TimeFormat.CLOCK_24H
                            else -> TimeFormat.CLOCK_12H
                        }
                    )
                    .setHour(endDateTime.hour)
                    .setMinute(0)
                    .setTitleText("Select ending time")
                    .setInputMode(INPUT_MODE_CLOCK)
                    .build()
            timePicker.addOnPositiveButtonClickListener {
                endDateTime = endDateTime.withHour(timePicker.hour).withMinute(timePicker.minute)
                if (endDateTime.isBefore(startDateTime)) {
                    startDateTime = endDateTime.minusHours(1)
                }
            }
            timePicker.show(supportFragmentManager, "timePicker")
        }

        binding.addEndDateTextView.setOnClickListener {
            calendar.set(
                endDateTime.year,
                endDateTime.month.value - 1,
                endDateTime.dayOfMonth
            )
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select end date")
                    .setSelection(calendar.timeInMillis)
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                endDateTime = LocalDateTime.ofEpochSecond(
                    (datePicker.selection!! / 1000),
                    0,
                    ZoneOffset.UTC
                ).withHour(endDateTime.hour).withMinute(endDateTime.minute)

                if (endDateTime.isBefore(startDateTime)) {
                    startDateTime =
                        endDateTime.withHour(startDateTime.hour).withMinute(startDateTime.minute)
                }
            }

            datePicker.show(supportFragmentManager, "datePicker")
        }

        val typedValue = TypedValue()
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorPrimaryContainer,
                typedValue,
                true
            )
        color = typedValue.data
        binding.addHSLColorPicker.setColor(color)

        binding.addHSLColorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                this@AddEventActivity.color = color
            }
        })


        binding.addBtnSave.setOnClickListener {
            if (binding.addTitleEditText.text.toString().isEmpty()) {
                val snackbar = Snackbar.make(it, "Event must have a title", Snackbar.LENGTH_SHORT)
                snackbar.setAction("Ok") {
                    snackbar.dismiss()
                }
                snackbar.show()
            } else {
                runOnIO {
                    dao.insertEvent(
                        CalEvent(
                            startDateTime,
                            endDateTime,
                            binding.addTitleEditText.text.toString(),
                            binding.addDescriptionEditText.text.toString(),
                            Color.valueOf(color)
                        )
                    )
                }
                finish()
            }
        }

        binding.addBtnCancel.setOnClickListener {
            finish()
        }
    }


}