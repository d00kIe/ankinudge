package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ThemePreference

class MainViewModel(private val repository: Repository = Repository.getRepository()): ViewModel() {
    private val preferences = repository.getUserPreferences()
    val newUser = Transformations.map(preferences) { it?.languages?.size == 0 ?: false }
}