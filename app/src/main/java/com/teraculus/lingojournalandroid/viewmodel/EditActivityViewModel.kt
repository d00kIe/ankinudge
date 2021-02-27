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
    val confidence = MutableLiveData(100)
    val motivation = MutableLiveData(100)

    init {
        val activity = id?.let { repository.getActivity(it) }
        if (activity != null) {
            date.value = activity.startDate
            title.value = activity.title
            text.value = activity.text
            confidence.value = activity.confidence
            motivation.value = activity.motivation
        }
    }

    fun addNote() {
        repository.addActivity(Activity(title.value!!, text.value!!, null, confidence.value!!, motivation.value!!, date.value!!))
    }

    fun updateNote(id: String) {
        repository.updateActivity(id, title.value!!, text.value!!,null, confidence.value!!, motivation.value!!, date.value!!)
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