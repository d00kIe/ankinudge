package com.teraculus.lingojournalandroid

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.teraculus.lingojournalandroid.databinding.ActivityEntryDetailsBindingImpl
import com.teraculus.lingojournalandroid.model.createDate
import com.teraculus.lingojournalandroid.viewmodel.EditNoteViewModel
import com.teraculus.lingojournalandroid.viewmodel.EditNoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var viewModel: EditNoteViewModel
    val args: AddNoteActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
                ViewModelProvider(this, EditNoteViewModelFactory(this, args.id)).get(EditNoteViewModel::class.java)
        val binding = DataBindingUtil.setContentView(this, R.layout.activity_entry_details) as ActivityEntryDetailsBindingImpl
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setSupportActionBar(findViewById(R.id.toolbar))

        // Back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dateTextView = findViewById<TextInputEditText>(R.id.editTextDate)
        val timeTextView = findViewById<TextInputEditText>(R.id.editTextTime)
        val dateFormatter = SimpleDateFormat("dd MMM yyyy")
        val timeFormatter = SimpleDateFormat("h:mm a")
        dateTextView.setText(dateFormatter.format(viewModel.noteDateTime.value))
        timeTextView.setText(timeFormatter.format(viewModel.noteDateTime.value))

        dateTextView.setOnClickListener {
            var c = Calendar.getInstance()
            c.time = viewModel.noteDateTime.value

            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                viewModel.noteDateTime.value = createDate(year, month, dayOfMonth, c.get(Calendar.HOUR), c.get(Calendar.MINUTE))
                dateTextView.setText(dateFormatter.format(viewModel.noteDateTime.value))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        timeTextView.setOnClickListener {
            var c = Calendar.getInstance()
            c.time = viewModel.noteDateTime.value

            TimePickerDialog(this, { _, hourOfDay, minute ->
                viewModel.noteDateTime.value = createDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hourOfDay, minute)
                timeTextView.setText(timeFormatter.format(viewModel.noteDateTime.value))
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true ).show()
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
        }
        else if (item.itemId == R.id.save_note_action) {
            if(args.id == null) {
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