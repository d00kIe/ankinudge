package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity

class ActivityDetailsViewModel(id: String,
    private val repository: Repository
) : ViewModel() {
    val activity : LiveData<Activity?> = repository.activities.get(id)
    val title = Transformations.map(activity) { it?.title }
    val text = Transformations.map(activity) { it?.text }
    val date = Transformations.map(activity) { it?.date }
    val startTime = Transformations.map(activity) { it?.startTime }
    val duration = Transformations.map(activity) { it?.duration }
    val unitCount = Transformations.map(activity) { it?.unitCount }
    val confidence = Transformations.map(activity) { it?.confidence }
    val motivation = Transformations.map(activity) { it?.motivation }
    val type = Transformations.map(activity) { it?.type }
    val typeName = Transformations.map(type) { it?.name }
    val category = Transformations.map(type) { it?.category }
    val categoryTitle = Transformations.map(category) { it?.title }
    val language = Transformations.map(activity) { it?.language }


    fun delete() {
        activity.value?.let { repository.activities.remove(it) }
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