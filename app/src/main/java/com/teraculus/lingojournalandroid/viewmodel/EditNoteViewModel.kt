package com.teraculus.lingojournalandroid.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Note
import java.time.LocalDateTime
import java.util.*

class EditNoteViewModel(private val repository: Repository, private val id: String?) : ViewModel() {
    val noteDateTime = MutableLiveData(Date());
    val noteTitle = MutableLiveData("")
    val noteText = MutableLiveData("")

    init {
        val note = id?.let { repository.getNoteById(it) }
        if (note != null) {
            noteDateTime.value = note.date
            noteTitle.value = note.title
            noteText.value = note.text
        }
    }

    fun addNote() {
        repository.addNote(Note(noteTitle.value!!, noteText.value!!, noteDateTime.value!!))
    }

    fun updateNote(id: String) {
        repository.updateNote(id, noteTitle.value!!, noteText.value!!, noteDateTime.value!!)
    }
}

class EditNoteViewModelFactory(private val context: Context, private val id: String?) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditNoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditNoteViewModel(Repository.getRepository(), id) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}