package com.teraculus.lingojournalandroid.viewmodel

import android.content.Context
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.launchEditGoalActivity
import com.teraculus.lingojournalandroid.model.*
import io.realm.RealmResults
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class GoalItemViewModel(
    private val frozenGoal: ActivityGoal,
    owner: LifecycleOwner,
    val repository: Repository = Repository.getRepository(),
) : ViewModel() {
    private val goal = Repository.getRepository().goals.get(frozenGoal.id.toString())
    val snapshot =
        MutableLiveData<ActivityGoal>(if (goal.value?.isValid == true) goal.value!!.freeze<ActivityGoal>() else null)

    val activities = Transformations.switchMap(snapshot) {
        it?.let { g ->
            if(g.type == GoalType.Daily) {
                val today = LocalDate.now()
                return@switchMap LiveRealmResults(repository.activities.all(today))
            } else {
                return@switchMap LiveRealmResults(repository.activities.all(g.date, g.endDate ?: LocalDate.now().plusYears(25))) // TODO: LocalDate.MAX crashes asDate
            }
        }
    }

    private val frozenActivities = Transformations.map(activities) {
        it?.let { a ->
            return@map (a as RealmResults<Activity>).freeze()
        }
    }

    val progress = MediatorLiveData<Float>().apply {
        fun update() {
            var res = 0f
            snapshot.value?.let { g ->
                val activities = frozenActivities.value.orEmpty()
                res = if(g.effortUnit == EffortUnit.Time) {
                    activities.filter { match(it, g) }.sumOf { a -> a.duration }.toFloat()
                } else {
                    activities.filter { match(it, g) }.sumOf { a -> a.unitCount.toDouble() }.toFloat()
                }
            }
            value = res
        }

        addSource(snapshot) { update() }
        addSource(frozenActivities) { update() }

        update()
    }


    val progressPercent = MediatorLiveData<Float>().apply {
        fun update() {
            var res = 0f
            snapshot.value?.let { g ->
                res = if(g.effortUnit == EffortUnit.Time) {
                    100f / (g.durationGoal?.toFloat() ?: 1f) * (progress.value ?: 0f)
                } else {
                    100f / (g.unitCountGoal ?: 1f) * (progress.value ?: 0f)
                }
            }
            value = res
        }

        addSource(snapshot) { update() }
        addSource(progress) { update() }

        update()
    }

    val goalWeekDaysString = Transformations.map(snapshot) {
        it?.weekDays?.sorted()?.map { d -> DayOfWeek.of(d) }
            ?.joinToString(truncated = ",") { d ->
                d.getDisplayName(TextStyle.SHORT,
                    Locale.getDefault())
            }
    }

    init {
        goal.observe(
            owner,
            Observer {
                snapshot.value = if (it?.isValid == true) it.freeze() else null
            }
        )
    }

    private fun match(activity: Activity, goal: ActivityGoal) : Boolean {
        return activity.language == goal.language && activity.type?.id == goal.activityType?.id
    }

    fun edit(context: Context) {
        launchEditGoalActivity(context, frozenGoal.id.toString())
    }

    fun delete() {
        repository.goals.remove(frozenGoal.id)
    }
}

class GoalItemViewModelFactory(
    private val rawGoal: ActivityGoal,
    private val owner: LifecycleOwner,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalItemViewModel(rawGoal, owner) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}