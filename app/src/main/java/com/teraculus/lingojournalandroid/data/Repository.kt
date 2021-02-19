package com.teraculus.lingojournalandroid.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teraculus.lingojournalandroid.models.Note
import com.teraculus.lingojournalandroid.models.notesData

class Repository {
    private val initialNoteList = notesData()
    private val noteList = MutableLiveData(initialNoteList)

    fun addNote(note: Note) {
        val currentList = noteList.value
        if(currentList == null) {
            noteList.postValue(listOf(note))
        } else {
            var updatedList = currentList.toMutableList()
            updatedList.add(note)
            noteList.postValue(updatedList)
        }
    }

    fun removeNote(note: Note) {
        var currentList = noteList.value
        if(currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(note)
            noteList.postValue(updatedList)
        }
    }

    fun getNoteById(id: String): Note? {
        return noteList.value?.let { notes ->
            return notes.firstOrNull{ it.id == id }
        }
    }

    fun getNotes(): LiveData<List<Note>> {
        return noteList
    }

    fun updateNote(note: Note) {
        val currentList = noteList.value
        if(currentList == null) {
            noteList.postValue(listOf(note))
        } else {
            var updatedList = currentList.toMutableList()
            val idx = updatedList.indexOf(getNoteById(note.id))
            updatedList[idx] = note
            noteList.postValue(updatedList)
        }
    }

    companion object {
        private  var INSTANCE: Repository? = null

        fun getRepository(): Repository {
            return synchronized(Repository::class) {
                var instance = INSTANCE ?: Repository()
                INSTANCE = instance
                instance
            }
        }
    }
}