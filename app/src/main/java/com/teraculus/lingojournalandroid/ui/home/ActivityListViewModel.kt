package com.teraculus.lingojournalandroid.ui.home

import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.LiveRealmObject
import com.teraculus.lingojournalandroid.ui.stats.DayData
import com.teraculus.lingojournalandroid.ui.stats.streakFromDate
import java.time.LocalDate

class LanguageDayData(val language: String, val data: List<DayData>)

class ActivityListViewModel(repository: Repository) : ViewModel() {
    private val activities = repository.getActivities()
    var grouped = Transformations.map(activities) { it?.groupBy { it1 -> it1.date } }
    var lastSevenDayData = Transformations.map(activities) { getGroupedLanguageDayData(it) }
    var streaks = Transformations.map(activities) { act -> act.orEmpty().groupBy { it.language }.mapValues { langact -> streakFromDate(langact.value, LocalDate.now(), true).size }  }

    private fun getGroupedLanguageDayData(activities: List<Activity>?): List<LanguageDayData> {
        return activities.orEmpty().groupBy { it.language }.map { LanguageDayData(it.key, getLastSevenDays(it.value)) }
    }

    private fun getLastSevenDays(activities: List<Activity>?): List<DayData> {
        val today = LocalDate.now()
        val sevenDaysAgo = today.minusDays(7)
        val result = getDefaultDayDataList(today)
        if(activities != null) {
            val lastSevenDays = activities.takeWhile { it.date >= sevenDaysAgo }.groupBy { it.date }
            for (d in result) {
                if(lastSevenDays.containsKey(LocalDate.of(d.year, d.month, d.day))) {
                    d.hasActivities = true
                }
            }
        }

        return result
    }

    private fun getDefaultDayData(day: LocalDate): DayData {
        return DayData(day.dayOfMonth, day.monthValue, day.year, true, day == LocalDate.now(), false, 0L, 0, 0L, 0)
    }

    private fun getDefaultDayDataList(today: LocalDate): List<DayData> {
        val sevenDaysAgo = today.minusDays(6)
        val result = mutableListOf<DayData>()
        for(d in 0 until 7) {
            result.add(getDefaultDayData(sevenDaysAgo.plusDays(d.toLong())))
        }
        return result
    }
}

class ActivityListViewModelFactory : ViewModelProvider.Factory {
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