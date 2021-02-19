package com.teraculus.lingojournalandroid

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.teraculus.lingojournalandroid.databinding.ActivityEntryDetailsBinding
import com.teraculus.lingojournalandroid.databinding.ActivityEntryDetailsBindingImpl
import com.teraculus.lingojournalandroid.viewmodels.EditNoteViewModel
import com.teraculus.lingojournalandroid.viewmodels.EditNoteViewModelFactory

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
        val noteTitleTextView = findViewById<TextInputEditText>(R.id.editNoteTitle)
        val noteTextTextView = findViewById<TextInputEditText>(R.id.editNoteText)

        dateTextView.setOnClickListener {
            DatePickerDialog(this, { view, year, month, dayOfMonth -> dateTextView.setText("Set") }, 2021, 1, 17).show()
        }

        timeTextView.setOnClickListener {
            TimePickerDialog(this, { view, hourOfDay, minute -> timeTextView.setText("Set") }, 4, 2, true ).show()
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