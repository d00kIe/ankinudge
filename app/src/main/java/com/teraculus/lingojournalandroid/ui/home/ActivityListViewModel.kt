package com.teraculus.lingojournalandroid.ui.home

import android.content.Context
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.LiveRealmObject

class ActivityListViewModel(repository: Repository) : ViewModel() {
    val activities = repository.getActivities()
    var grouped = Transformations.map(activities) { it?.groupBy { it1 -> it1.date } }
}

class ActivityListViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ActivityListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityListViewModel(Repository.getRepository()) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}


class ActivityItemViewModel(rawActivity: Activity) : ViewModel() {
    val activity : LiveRealmObject<Activity> = LiveRealmObject(rawActivity)
    val id = Transformations.map(activity) { it.id }
    val title = Transformations.map(activity) { it.title }
    val text = Transformations.map(activity) { it.text }
    val date = Transformations.map(activity) { it.date }
    val startTime = Transformations.map(activity) { it.startTime }
    val endTime = Transformations.map(activity) { it.endTime }
    val confidence = Transformations.map(activity) { it.confidence }
    val motivation = Transformations.map(activity) { it.motivation }
    val type = Transformations.map(activity) { it.type }
    val category = Transformations.map(type) { it?.category }
    val language = Transformations.map(activity) { it.language }

}

class ActivityItemViewModelFactory(private val rawActivity: Activity) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ActivityItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityItemViewModel(rawActivity) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}