package com.teraculus.lingojournalandroid.ui.home

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityGoal
import com.teraculus.lingojournalandroid.model.transform
import com.teraculus.lingojournalandroid.viewmodel.DayData
import com.teraculus.lingojournalandroid.viewmodel.streakFromDate
import io.realm.RealmResults
import java.time.LocalDate

class LanguageDayData(val language: String, val data: List<DayData>)

class ActivityListViewModel(repository: Repository) : ViewModel() {
    private val activities = repository.getActivities()
    private val goals = repository.getActivityGoals()
    val frozenGoals = Transformations.map(goals) { (it as RealmResults<ActivityGoal>).freeze().sortedByDescending { g -> g.id.timestamp } }
    val frozen = Transformations.map(activities) { (it as RealmResults<Activity>).freeze() }
    var grouped = Transformations.map(frozen) {
        it?.groupBy { it1 -> it1.date }.orEmpty()
            .mapValues { it2 -> it2.value.sortedByDescending { a -> a.startTime } }
    }
    var lastSevenDayData =
        frozen.transform(scope = viewModelScope) { getGroupedLanguageDayData(it) }
    var streaks = frozen.transform(scope = viewModelScope) { act ->
        act.orEmpty().groupBy { it.language }
            .mapValues { langact -> streakFromDate(langact.value, LocalDate.now(), true).size }
    }

    private val todayActivities = Transformations.map(grouped) { it[LocalDate.now()] }
    val todayGoals = MediatorLiveData<List<ActivityGoal>>().apply {
        fun update() {
            val today = LocalDate.now()
            val todaysActivities = todayActivities.value.orEmpty()
            value = frozenGoals.value.orEmpty().filter { g ->
                g.active && g.weekDays.contains(today.dayOfWeek.value) && todaysActivities.none { a -> a.language == g.language && a.type?.id == g.activityType?.id }
            }
        }

        addSource(todayActivities) { update() }
        addSource(frozenGoals) { update() }

        update()
    }

    private fun getGroupedLanguageDayData(activities: List<Activity>?): List<LanguageDayData> {
        return activities.orEmpty().groupBy { it.language }
            .map { LanguageDayData(it.key, getLastSevenDays(it.value)) }
    }

    private fun getLastSevenDays(activities: List<Activity>?): List<DayData> {
        val today = LocalDate.now()
        val sevenDaysAgo = today.minusDays(7)
        val result = getDefaultDayDataList(today)
        if (activities != null) {
            val lastSevenDays = activities.takeWhile { it.date >= sevenDaysAgo }.groupBy { it.date }
            for (d in result) {
                if (lastSevenDays.containsKey(LocalDate.of(d.year, d.month, d.day))) {
                    d.hasActivities = true
                }
            }
        }

        return result
    }

    private fun getDefaultDayData(day: LocalDate): DayData {
        return DayData(day.dayOfMonth,
            day.monthValue,
            day.year,
            true,
            day == LocalDate.now(),
            false,
            0L,
            0,
            0L,
            0)
    }

    private fun getDefaultDayDataList(today: LocalDate): List<DayData> {
        val sevenDaysAgo = today.minusDays(6)
        val result = mutableListOf<DayData>()
        for (d in 0 until 7) {
            result.add(getDefaultDayData(sevenDaysAgo.plusDays(d.toLong())))
        }
        return result
    }
}

class ActivityListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityListViewModel(Repository.getRepository()) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}


class ActivityItemViewModel(frozenActivity: Activity, owner: LifecycleOwner) : ViewModel() {
    val activity = Repository.getRepository().getActivity(frozenActivity.id.toString())
    val snapshot =
        MutableLiveData<Activity>(if (activity.value?.isValid == true) activity.value!!.freeze<Activity>() else null)

    init {
        activity.observe(
            owner,
            Observer {
                snapshot.value = if (it?.isValid == true) it.freeze() else null
            }
        )
    }
}

class ActivityItemViewModelFactory(
    private val rawActivity: Activity,
    private val owner: LifecycleOwner,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActivityItemViewModel(rawActivity, owner) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}