package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import java.util.*

class EditActivityViewModel(private val repository: Repository) : ViewModel() {
    val startDate = MutableLiveData(Date())
    val endDate = MutableLiveData(Date())
    val title = MutableLiveData("")
    val text = MutableLiveData("")
    val confidence = MutableLiveData(100)
    val motivation = MutableLiveData(100)

    fun prepareActivity(id: String?) {
        val activity = id?.let { repository.getActivity(it) }
        if (activity != null) {
            title.value = activity.title
            text.value = activity.text
            confidence.value = activity.confidence
            motivation.value = activity.motivation
            startDate.value = activity.startDate
            endDate.value = activity.endDate
        } else {
            title.value = ""
            text.value = ""
            confidence.value = 50
            motivation.value = 50
            startDate.value = Date()
            endDate.value = Date()
        }
    }

    fun onTitleChange(value: String) {
        title.value = value
    }

    fun onTextChange(value: String) {
        text.value = value
    }

    fun onConfidenceChange(value: Float) {
        confidence.value = value.toInt()
    }

    fun onMotivationChange(value: Float) {
        motivation.value = value.toInt()
    }

    fun addNote() {
        repository.addActivity(Activity(title.value!!, text.value!!, null, confidence.value!!, motivation.value!!, startDate.value!!))
    }

    fun updateNote(id: String) {
        repository.updateActivity(id, title.value!!, text.value!!,null, confidence.value!!, motivation.value!!, startDate.value!!)
    }
}

class EditActivityViewModelFactory() :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditActivityViewModel(Repository.getRepository()) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}