package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.PickerProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.data.getAllLanguages
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityType
import com.teraculus.lingojournalandroid.utils.getMinutes
import java.time.LocalDate
import java.time.LocalTime

class ActivityDetailsViewModel(id: String,
    private val repository: Repository
) : ViewModel() {
    val activity : LiveData<Activity?> = repository.getActivity(id)
    val title = Transformations.map(activity) { it?.title }
    val text = Transformations.map(activity) { it?.text }
    val date = Transformations.map(activity) { it?.date }
    val startTime = Transformations.map(activity) { it?.startTime }
    val endTime = Transformations.map(activity) { it?.endTime }
    val confidence = Transformations.map(activity) { it?.confidence }
    val motivation = Transformations.map(activity) { it?.motivation }
    val type = Transformations.map(activity) { it?.type }
    val language = Transformations.map(activity) { it?.language }


    fun delete() {
        activity.value?.let { repository.removeActivity(it) }
    }
}

class ActivityDetailsViewModelFactory(val id: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityDetailsViewModel(id, Repository.getRepository()) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}