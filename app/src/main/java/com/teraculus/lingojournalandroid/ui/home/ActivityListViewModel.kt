package com.teraculus.lingojournalandroid.ui.home

import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.LiveRealmObject

class ActivityListViewModel(repository: Repository) : ViewModel() {
    private val activities = repository.getActivities()
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


class ActivityItemViewModel(rawActivity: Activity, owner: LifecycleOwner) : ViewModel() {
    val activity : LiveRealmObject<Activity> = LiveRealmObject(rawActivity)
    val snapshot = MutableLiveData<Activity>(if(activity.value?.isValid == true) activity.value!!.freeze<Activity>() else null)
    init {
        activity.observe(
            owner,
            Observer {
                snapshot.value = if (it.isValid) it.freeze() else null
            }
        )
    }
}

class ActivityItemViewModelFactory(private val rawActivity: Activity, private val owner: LifecycleOwner) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ActivityItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityItemViewModel(rawActivity, owner) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}