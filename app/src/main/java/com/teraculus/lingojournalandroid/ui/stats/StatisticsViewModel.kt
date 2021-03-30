package com.teraculus.lingojournalandroid.ui.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityCategory
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

fun getMinutes(activity: Activity): Long {
    return abs(Duration.between(activity.startTime, activity.endTime).toMinutes())
}

class ActivityCategoryStat(val category: ActivityCategory?, activities: List<Activity>) {
    val minutes: Long = activities.map { it -> getMinutes(it) }.sum()
    val count: Int = activities.size
    val confidence: Float = activities.map { it -> it.confidence }.average().toFloat()
    val motivation: Float = activities.map { it -> it.motivation }.average().toFloat()
}

class LanguageStatData(val language: String, val categoryStats: List<ActivityCategoryStat>, val maxMinutes: Long = 0L, val maxCount: Int = 0) {
    val allMinutes: Long = if(categoryStats.isNotEmpty()) categoryStats.map { it -> it.minutes }.sum() else 0
    val allCount: Int = if(categoryStats.isNotEmpty()) categoryStats.map { it -> it.count }.sum() else 0
    val allConfidence: Float = if(categoryStats.isNotEmpty()) categoryStats.map { it -> it.confidence }.average().toFloat() else 0f
    val allMotivation: Float = if(categoryStats.isNotEmpty()) categoryStats.map { it -> it.motivation }.average().toFloat() else 0f

    companion object {
        fun empty(): LanguageStatData {
            return LanguageStatData("", listOf<ActivityCategoryStat>())
        }
    }
}
class StatisticsViewModel(val repository: Repository) : ViewModel() {
    val activities = LiveRealmResults<Activity>(null)
    val stats = MutableLiveData<List<LanguageStatData>?>()
    val range = MutableLiveData(StatisticRange.MONTH)
    val rangeIndex = Transformations.map(range) { it.index }
    val day = MutableLiveData(LocalDate.now())
    val month = MutableLiveData(YearMonth.now())
    var maxMinutes = MutableLiveData<Long>(0L)
    var maxCount = MutableLiveData(0)

    init {
        activities.observeForever {
            stats.value = it?.let { it1 -> mapToStats(it1) }
            if(!stats.value.isNullOrEmpty()) {
                maxMinutes.value = stats.value.orEmpty().maxOf { it2 -> it2.maxMinutes }
                maxCount.value = stats.value.orEmpty().maxOf { it2 -> it2.maxCount }
            } else {
                maxMinutes.value = 0L
                maxCount.value = 0
            }
        }
        setMonth(YearMonth.now())
    }

    fun setRangeIndex(idx: Int) {
        when(idx) {
            0 -> setDay(day.value!!)
            1 -> setMonth(month.value!!)
            2 -> setAllTime()
        }
    }

    fun setDay(date: LocalDate) {
        range.value = StatisticRange.DAY
        day.value = date
        activities.reset(repository.getActivities(date))
    }

    fun setMonth(yearMonth: YearMonth) {
        range.value = StatisticRange.MONTH
        month.value = yearMonth
        val from = LocalDate.of(yearMonth.year, yearMonth.month, 1)
        val to = from.withDayOfMonth(yearMonth.lengthOfMonth())
        activities.reset(repository.getActivities(from, to))
    }

    fun setAllTime() {
        range.value = StatisticRange.ALL
        activities.reset(repository.getAllActivities())
    }

    private fun groupByLanguage(items: List<Activity>?): Map<String, List<Activity>>? {
        return items?.groupBy { it -> it.language }
    }

    private fun groupByType(items: List<Activity>?): Map<ActivityCategory?, List<Activity>>? {
        return items?.groupBy { it -> it.type?.category }
    }

    private fun mapToStats(items: List<Activity>): List<LanguageStatData>? {
        return groupByLanguage(activities.value)?.map {
            val byType = groupByType(it.value)
            val typeStats = byType?.map { it ->
                ActivityCategoryStat(it.key, it.value)
            }
            val groupByDay = items.groupBy { it -> it.date }
            LanguageStatData(it.key, typeStats!!, groupByDay.values.maxOf { it -> it.sumOf { it1 -> getMinutes(it1) } }, groupByDay.maxOf { it -> it.value.size })
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