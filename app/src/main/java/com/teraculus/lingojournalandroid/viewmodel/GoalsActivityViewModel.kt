package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ActivityGoal
import io.realm.RealmResults


class GoalsActivityViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val goals = repository.getActivityGoals()
    val frozen = Transformations.map(goals) {
        (it as RealmResults<ActivityGoal>).freeze().sortedByDescending { g -> g.id.timestamp }
    }
    val preferences = repository.getUserPreferences()
    val types = repository.getTypes()
}