package com.teraculus.lingojournalandroid.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import java.util.*

class EditActivityViewModel(private val repository: Repository, id: String?) : ViewModel() {
    val date = MutableLiveData(Date())
    val title = MutableLiveData("")
    val text = MutableLiveData("")

    init {
        val note = id?.let { repository.getActivity(it) }
        if (note != null) {
            date.value = note.date
            title.value = note.title
            text.value = note.text
        }
    }

    fun addNote() {
        repository.addActivity(Activity(title.value!!, text.value!!, date.value!!))
    }

    fun updateNote(id: String) {
        repository.updateActivity(id, title.value!!, text.value!!, date.value!!)
    }
}

class EditActivityViewModelFactory(private val context: Context, private val id: String?) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditActivityViewModel(Repository.getRepository(), id) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}