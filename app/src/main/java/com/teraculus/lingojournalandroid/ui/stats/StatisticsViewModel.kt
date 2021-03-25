package com.teraculus.lingojournalandroid.ui.stats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityType
import com.teraculus.lingojournalandroid.model.LiveRealmResults
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

enum class StatisticRange(val title: String, val index: Int) {
    DAY("Day", 0),
    MONTH("Month", 1),
    ALL("All time", 2)
}

fun getHours(activity: Activity): Float {
    return abs(Duration.between(activity.startTime, activity.endTime).toMinutes() / 60f)
}

class ActivityTypeStat(val type: ActivityType?, activities: List<Activity>) {
    val hours: Float = activities.map { it -> getHours(it) }.sum()
    val count: Int = activities.size
    val confidence: Float = activities.map { it -> it.confidence }.average().toFloat()
    val motivation: Float = activities.map { it -> it.motivation }.average().toFloat()
}

class LanguageStatData(val language: String, typeStats: List<ActivityTypeStat>) {
    val allHours: Float = typeStats.map { it -> it.hours }.sum()
    val allCount: Int = typeStats.map { it -> it.count }.sum()
    val allConfidence: Float = typeStats.map { it -> it.confidence }.average().toFloat()
    val allMotivation: Float = typeStats.map { it -> it.motivation }.average().toFloat()
}


class StatisticsViewModel(val repository: Repository) : ViewModel() {
    val activities = LiveRealmResults<Activity>(null)
    val range = MutableLiveData<StatisticRange>(StatisticRange.MONTH)
    val stats = MutableLiveData<List<LanguageStatData>?>()

    init {
        activities.observeForever {
            stats.value = activities.value?.let { it1 -> mapToStats(it1) }
        }
        setMonth(YearMonth.now())
    }

    fun setDay(date: LocalDate) {
        range.value = StatisticRange.DAY
        activities.reset(repository.getActivities(date))
    }

    fun setMonth(month: YearMonth) {
        range.value = StatisticRange.MONTH
        val from = LocalDate.of(month.year, month.month, 1)
        val to = from.withDayOfMonth(month.lengthOfMonth())
        activities.reset(repository.getActivities(from, to))
    }

    fun setAllTime() {
        range.value = StatisticRange.ALL
        activities.reset(repository.getAllActivities())
    }

    fun groupByLanguage(items: List<Activity>?): Map<String, List<Activity>>? {
        return items?.groupBy { it -> it.language }
    }

    fun groupByType(items: List<Activity>?): Map<ActivityType?, List<Activity>>? {
        return items?.groupBy { it -> it.type }
    }

    fun mapToStats(items: List<Activity>): List<LanguageStatData>? {
        return groupByLanguage(activities.value)?.map {
            val byType = groupByType(it.value)
            val typeStats = byType?.map { it ->
                ActivityTypeStat(it.key, it.value)
            }
            LanguageStatData(it.key, typeStats!!)
        }
    }
}

class StatisticsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(Repository.getRepository()) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}