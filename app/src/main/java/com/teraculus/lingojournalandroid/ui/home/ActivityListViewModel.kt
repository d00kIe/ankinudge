package com.teraculus.lingojournalandroid.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teraculus.lingojournalandroid.data.Repository

class ActivityListViewModel(repository: Repository) : ViewModel() {
    val activities = repository.getActivities()
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