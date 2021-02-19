package com.teraculus.lingojournalandroid.viewmodels

import android.content.Context
import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.models.Note
import java.time.LocalDateTime

class EditNoteViewModel(private val repository: Repository, private val id: String?) : ViewModel() {
    val noteDateTime = MutableLiveData(LocalDateTime.now());
    val noteTitle = MutableLiveData("")
    val noteText = MutableLiveData("")

    init {
        val note = id?.let { repository.getNoteById(it) }
        if (note != null) {
            noteDateTime.value = note.dateTime
            noteTitle.value = note.title
            noteText.value = note.note
        }
    }

    fun addNote() {
        repository.addNote(Note(noteTitle.value!!, noteText.value!!, noteDateTime.value!!))
    }

    fun updateNote(id: String) {
        val note = repository.getNoteById(id)
        if (note != null) {
            note.title = noteTitle.value!!
            note.note = noteText.value!!
            note.dateTime = noteDateTime.value!!
        }
        note?.let { repository.updateNote(it) }
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