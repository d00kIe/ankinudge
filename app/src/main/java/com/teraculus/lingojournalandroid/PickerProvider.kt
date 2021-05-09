package com.teraculus.lingojournalandroid

import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_12H
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*

class PickerProvider {
    private lateinit var _fragmentManagerProvider: () -> FragmentManager
    var fragmentManagerProvider: () -> FragmentManager
        get() {
            return _fragmentManagerProvider
        }
        set(value) {
            _fragmentManagerProvider = value
        }

    fun pickDate(title: CharSequence?, initialDate: LocalDate, onDateChange: (changedDate: LocalDate) -> Unit) {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder
            .setTitleText(title)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTheme(MaterialDatePicker.STYLE_NORMAL)
            .setSelection(initialDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
            .build()

        picker.addOnPositiveButtonClickListener {
            val newDate = Calendar.getInstance()
            newDate.timeInMillis = it

            onDateChange(LocalDate.of(
                newDate.get(Calendar.YEAR),
                newDate.get(Calendar.MONTH) + 1,
                newDate.get(Calendar.DAY_OF_MONTH))
            )
        }

        picker.show(fragmentManagerProvider(), picker.toString())
    }

    fun pickTime(title: CharSequence?, initialTime: LocalTime, onTimeChange: (changedTime: LocalTime) -> Unit) {
        val builder = MaterialTimePicker.Builder()
        val picker =
            builder
                .setTitleText(title)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTimeFormat(CLOCK_12H)
                .setHour(initialTime.hour)
                .setMinute(initialTime.minute)
                .build()

        picker.addOnPositiveButtonClickListener {
            onTimeChange(LocalTime.of(
                picker.hour,
                picker.minute
            ))
        }

        picker.show(fragmentManagerProvider(), picker.toString())
    }

    fun pickDuration(title: CharSequence?, initialTime: LocalTime, onTimeChange: (changedTime: LocalTime) -> Unit) {
        val builder = MaterialTimePicker.Builder()
        val picker =
            builder
                .setTitleText(title)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTimeFormat(CLOCK_24H)
                .setHour(initialTime.hour)
                .setMinute(initialTime.minute)
                .build()

        picker.addOnPositiveButtonClickListener {
            onTimeChange(LocalTime.of(
                picker.hour,
                picker.minute
            ))
        }

        picker.show(fragmentManagerProvider(), picker.toString())
    }


    companion object {
        private var INSTANCE: PickerProvider? = null

        fun getPickerProvider(): PickerProvider {
            return synchronized(PickerProvider::class) {
                val instance = INSTANCE ?: PickerProvider()
                INSTANCE = instance
                instance
            }
        }
    }
}