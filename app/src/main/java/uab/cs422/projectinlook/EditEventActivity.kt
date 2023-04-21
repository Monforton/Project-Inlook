package uab.cs422.projectinlook

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import uab.cs422.projectinlook.database.EventDao
import uab.cs422.projectinlook.database.EventDatabase
import uab.cs422.projectinlook.databinding.ActivityEditEventBinding
import uab.cs422.projectinlook.database.entities.CalEvent
import uab.cs422.projectinlook.ui.dialogs.DeletionBottomSheet
import uab.cs422.projectinlook.util.hourFormatter
import uab.cs422.projectinlook.util.runOnIO
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class EditEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditEventBinding
    private lateinit var dao: EventDao
    private lateinit var editEventViewModel: EditEventViewModel
    private lateinit var longerFormat: DateTimeFormatter
    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    private var startDateTime: LocalDateTime = LocalDateTime.now().withMinute(0).plusHours(1)
        set(value) {
            field = value
            editEventViewModel.setStartTimeText(field.format(longerFormat))
            editEventViewModel.setStartDateText(buildString {
                append(startDateTime.format(DateTimeFormatter.ofPattern("E, MMM dd")))
                append("\n")
                append(startDateTime.format(DateTimeFormatter.ofPattern("yyyy")))
            })
        }
    private var endDateTime: LocalDateTime = startDateTime.plusHours(1)
        set(value) {
            field = value
            editEventViewModel.setEndTimeText(field.format(longerFormat))
            editEventViewModel.setEndDateText(buildString {
                append(endDateTime.format(DateTimeFormatter.ofPattern("E, MMM dd")))
                append("\n")
                append(endDateTime.format(DateTimeFormatter.ofPattern("yyyy")))
            })
        }
    private var color: Int = 0
        set(value) {
            field = value
            editEventViewModel.setEventColor(value)
        }
    private var title: String = "Title"
        set(value) {
            field = value
            editEventViewModel.setTitleText(value)
        }
    private var event: CalEvent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        longerFormat = hourFormatter(this, true)

        editEventViewModel = ViewModelProvider(this)[EditEventViewModel::class.java]
        editEventViewModel.startTimeText.observe(this) {
            binding.editStartTimeTextView.text = it
        }
        editEventViewModel.startDateText.observe(this) {
            binding.editStartDateTextView.text = it
        }
        editEventViewModel.endTimeText.observe(this) {
            binding.editEndTimeTextView.text = it
        }
        editEventViewModel.endDateText.observe(this) {
            binding.editEndDateTextView.text = it
        }
        editEventViewModel.titleText.observe(this) {
            binding.editEventPreview.text = it
        }
        editEventViewModel.eventColor.observe(this) {
            binding.editEventPreview.backgroundTintList = ColorStateList.valueOf(it)
            binding.editEventPreview.setTextColor(
                if (Color.luminance(it) > 0.5) Color.BLACK else Color.WHITE
            )
        }
        startDateTime = LocalDateTime.now().withMinute(0)
        endDateTime = startDateTime.plusHours(1)

        dao = EventDatabase.getInstance(this).eventDao

        binding.editStartTimeTextView.setOnClickListener {
            val timePicker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(
                        when (PreferenceManager.getDefaultSharedPreferences(this)
                            .getString("hour_format", "")) {
                            "clock_time_format" -> TimeFormat.CLOCK_12H
                            "day_time_format" -> TimeFormat.CLOCK_24H
                            "local_time_format" -> {
                                if (android.text.format.DateFormat.is24HourFormat(this))
                                    TimeFormat.CLOCK_24H
                                else
                                    TimeFormat.CLOCK_12H
                            }
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

        binding.editStartDateTextView.setOnClickListener {
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

        binding.editEndTimeTextView.setOnClickListener {
            val timePicker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(
                        when (PreferenceManager.getDefaultSharedPreferences(this)
                            .getString("hour_format", "")) {
                            "clock_time_format" -> TimeFormat.CLOCK_12H
                            "day_time_format" -> TimeFormat.CLOCK_24H
                            "local_time_format" -> {
                                if (android.text.format.DateFormat.is24HourFormat(this))
                                    TimeFormat.CLOCK_24H
                                else
                                    TimeFormat.CLOCK_12H
                            }
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

        binding.editEndDateTextView.setOnClickListener {
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

        binding.editTitleEditText.setOnFocusChangeListener { _, _ ->
            title = binding.editTitleEditText.text.toString()
        }

        val typedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimaryContainer,
            typedValue,
            true
        )
        color = typedValue.data
        binding.editHSLColorPicker.setColor(color)

        binding.editHSLColorPicker.setColorSelectionListener(object :
            SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                this@EditEventActivity.color = color
            }
        })
        binding.editEventPreview.backgroundTintList = ColorStateList.valueOf(color)
        binding.editEventPreview.setTextColor(
            if (Color.luminance(color) > 0.5) Color.BLACK else Color.WHITE
        )


        binding.editBtnSave.setOnClickListener {
            if (binding.editTitleEditText.text.toString().isEmpty()) {
                val snackbar = Snackbar.make(it, "Event must have a title", Snackbar.LENGTH_SHORT)
                snackbar.setAction("Ok") {
                    snackbar.dismiss()
                }
                snackbar.show()
            } else {
                event?.let {
                    runOnIO {
                        it.reconstruct(
                            startDateTime,
                            endDateTime,
                            binding.editTitleEditText.text.toString(),
                            binding.editDescriptionEditText.text.toString(),
                            Color.valueOf(color)
                        )
                        dao.updateEvent(it)
                    }
                } ?: runOnIO {
                    dao.insertEvent(
                        CalEvent(
                            startDateTime,
                            endDateTime,
                            binding.editTitleEditText.text.toString(),
                            binding.editDescriptionEditText.text.toString(),
                            Color.valueOf(color)
                        )
                    )
                }
                finish()
            }
        }

        binding.editBtnCancel.setOnClickListener {
            finish()
        }

        binding.editBtnDelete.setOnClickListener {
            val deletionBottomSheet = DeletionBottomSheet()
            deletionBottomSheet.neutralAction = {
                deletionBottomSheet.dismiss()
            }
            deletionBottomSheet.deleteAction = {
                runOnIO {
                    dao.deleteEvent(event!!)
                }
                deletionBottomSheet.dismiss()
                finish()
            }
            deletionBottomSheet.show(supportFragmentManager, DeletionBottomSheet.TAG)
        }

        if (intent.hasExtra("event_data")) {
            event = intent.getParcelableExtra("event_data")
            title = event?.title!!
            event?.getColorAsColor()!!.toArgb()
            binding.editTitleEditText.setText(event?.title)
            startDateTime = event?.getStartAsLocalDateTime()!!
            endDateTime = event?.getEndAsLocalDateTime()!!
            color = event?.getColorAsColor()!!.toArgb()
            binding.editHSLColorPicker.setColor(event?.getColorAsColor()!!.toArgb())
        } else {
            binding.editButtonsLayout.removeView(binding.editBtnDelete)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.editTitleEditText.clearFocus()
    }
}