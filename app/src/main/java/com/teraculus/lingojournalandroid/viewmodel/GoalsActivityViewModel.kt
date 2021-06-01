package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ActivityGoal
import io.realm.RealmResults


class GoalsActivityViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val goals = repository.goals.all()
    val frozen = Transformations.map(goals) {
        (it as RealmResults<ActivityGoal>).freeze().sortedByDescending { g -> g.id.timestamp }
    }
    val preferences = repository.preferences.all()
    val types = repository.types.all()
}