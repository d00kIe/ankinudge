package com.teraculus.lingojournalandroid

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.teraculus.lingojournalandroid.databinding.ActivityEntryDetailsBindingImpl
import com.teraculus.lingojournalandroid.model.createDate
import com.teraculus.lingojournalandroid.viewmodel.EditNoteViewModel
import com.teraculus.lingojournalandroid.viewmodel.EditNoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AddNoteActivity : AppCompatActivity() {
    private lateinit var viewModel: EditNoteViewModel
    val args: AddNoteActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this, EditNoteViewModelFactory(this, args.id)).get(
                EditNoteViewModel::class.java
            )
        val binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_entry_details
        ) as ActivityEntryDetailsBindingImpl
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Back button

        setupDateTimeViews()

        val languageTextView = findViewById<TextInputEditText>(R.id.editLanguage)
        languageTextView.setOnClickListener {
            val items = arrayOf("English", "German", "Bulgarian", "Italian", "Spanish", "Russian", "Romanian", "Chinese", "Turkish", "Norwegian", "Estonian")
            val builder = MaterialAlertDialogBuilder(this)
            builder
                .setTitle(R.string.choose_language)
                .setItems(items) { dialog, which ->
                languageTextView.setText(items[which])
                dialog.dismiss()
            }
                .show()
        }
    }

    private fun setupDateTimeViews() {
        val dateTextView = findViewById<TextInputEditText>(R.id.editTextDate)
        val timeTextView = findViewById<TextInputEditText>(R.id.editTextTime)
        val dateFormatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM)
        val timeFormatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM)
        dateTextView.setText(dateFormatter.format(viewModel.noteDateTime.value))
        timeTextView.setText(timeFormatter.format(viewModel.noteDateTime.value))

        dateTextView.setOnClickListener {
            val c = Calendar.getInstance()
            c.time = viewModel.noteDateTime.value

            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setTheme(MaterialDatePicker.STYLE_NORMAL).setSelection(c.timeInMillis).build()

            picker.addOnPositiveButtonClickListener {
                val newDate = Calendar.getInstance()
                newDate.timeInMillis = it
                viewModel.noteDateTime.value = createDate(
                    newDate.get(Calendar.YEAR),
                    newDate.get(Calendar.MONTH),
                    newDate.get(Calendar.DAY_OF_MONTH),
                    c.get(Calendar.HOUR),
                    c.get(Calendar.MINUTE)
                )
                dateTextView.setText(dateFormatter.format(viewModel.noteDateTime.value))
            }

            picker.show(supportFragmentManager, picker.toString())
        }

        timeTextView.setOnClickListener {
            val c = Calendar.getInstance()
            c.time = viewModel.noteDateTime.value

            val builder = MaterialTimePicker.Builder()
            val isSystem24Hour = is24HourFormat(this)
            val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
            val picker =
                builder.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).setTimeFormat(clockFormat)
                    .setHour(c.get(Calendar.HOUR)).setMinute(c.get(Calendar.MINUTE)).build()

            picker.addOnPositiveButtonClickListener {
                viewModel.noteDateTime.value = createDate(
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH),
                    picker.hour,
                    picker.minute
                )
                timeTextView.setText(timeFormatter.format(viewModel.noteDateTime.value))
            }

            picker.show(supportFragmentManager, picker.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_entry_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        } else if (item.itemId == R.id.save_note_action) {
            if (args.id == null) {
                viewModel.addNote()
            } else {
                viewModel.updateNote(args.id!!)
            }
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}