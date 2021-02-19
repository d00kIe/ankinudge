package com.teraculus.lingojournalandroid.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.models.Note
import java.time.LocalDateTime

class NoteListViewModel(private val repository: Repository) : ViewModel() {
    val notesLiveData = repository.getNotes()

    fun addNote(title: String = "", note: String = "", dateTime: LocalDateTime) {
        repository.addNote(Note(title, note, dateTime))
    }
}

class NoteListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NoteListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteListViewModel(Repository.getRepository()) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}