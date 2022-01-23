package com.codewithdimi.app_template.viewmodel

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codewithdimi.app_template.data.Repository

class MainViewModel(repository: Repository = Repository.getRepository()): ViewModel() {
    private val preferences = repository.preferences.all()
    val newUser = Transformations.map(preferences) { it?.languages?.size == 0 }
}