package com.teraculus.lingojournalandroid

import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.utils.localDateToDate
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class PickerProvider() {
    private lateinit var _fragmentManager: FragmentManager
    var fragmentManager: FragmentManager
        get() {
            return _fragmentManager
        }
        set(value) {
            _fragmentManager = value
        }

    fun pickDate(title: CharSequence?, initialDate: LocalDate, onDateChange: (changedDate: LocalDate) -> Unit) {
        val c = Calendar.getInstance()
        c.time = localDateToDate(initialDate)

        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder
            .setTitleText(title)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTheme(MaterialDatePicker.STYLE_NORMAL)
            .setSelection(c.timeInMillis)
            .build()

        picker.addOnPositiveButtonClickListener {
            val newDate = Calendar.getInstance()
            newDate.timeInMillis = it

            onDateChange(LocalDate.of(
                newDate.get(Calendar.YEAR),
                newDate.get(Calendar.MONTH),
                newDate.get(Calendar.DAY_OF_MONTH))
            )
        }

        picker.show(fragmentManager, picker.toString())
    }

    fun pickTime(title: CharSequence?, initialTime: LocalTime, onTimeChange: (changedTime: LocalTime) -> Unit) {
        val builder = MaterialTimePicker.Builder()
        val picker =
            builder
                .setTitleText(title)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setHour(initialTime.hour)
                .setMinute(initialTime.minute)
                .build()

        picker.addOnPositiveButtonClickListener {
            onTimeChange(LocalTime.of(
                picker.hour,
                picker.minute
            ))
        }

        picker.show(fragmentManager, picker.toString())
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