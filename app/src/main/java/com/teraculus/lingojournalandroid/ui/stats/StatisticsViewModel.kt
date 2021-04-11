package com.teraculus.lingojournalandroid.ui.stats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityCategory
import com.teraculus.lingojournalandroid.model.LiveRealmResults
import com.teraculus.lingojournalandroid.utils.getMinutes
import java.time.LocalDate
import java.time.YearMonth


// Ideas:
// Language split
// Activitie types split per category
// Compared to previous day/month
// Achieved level : Add additional button to add achieved language competency
// Streak per activity category
// Average daily tasks
enum class StatisticRange(val title: String, val index: Int) {
    DAY("Day", 0),
    MONTH("Month", 1),
    ALL("All time", 2)
}

class ActivityCategoryStat(val category: ActivityCategory?, activities: List<Activity>) {
    val minutes: Long = activities.map { getMinutes(it) }.sum()
    val count: Int = activities.size
    val confidence: Float = activities.map { it.confidence }.average().toFloat()
    val motivation: Float = activities.map { it.motivation }.average().toFloat()
}

open class LanguageStatData(
    val language: String,
    val categoryStats: List<ActivityCategoryStat>,
    val maxMinutes: Long = 0L,
    val maxCount: Int = 0,
) {
    val allMinutes: Long =
        if (categoryStats.isNotEmpty()) categoryStats.map { it.minutes }.sum() else 0
    val allCount: Int =
        if (categoryStats.isNotEmpty()) categoryStats.map { it.count }.sum() else 0
    val allConfidence: Float =
        if (categoryStats.isNotEmpty()) categoryStats.filter { !it.confidence.isNaN() }.map { it.confidence }.average()
            .toFloat() else 0f
    val allMotivation: Float =
        if (categoryStats.isNotEmpty()) categoryStats.filter { !it.motivation.isNaN() }.map { it.motivation }.average()
            .toFloat() else 0f

    companion object {
        fun empty(): LanguageStatData {
            return LanguageStatData("", listOf())
        }
    }
}

class DayCategoryStreakData(val category: ActivityCategory?, val streak: Int)

class DayLanguageStreakData (
    language: String,
    categoryStats: List<ActivityCategoryStat>,
    streakMap:  Map<LocalDate, List<Activity>>
) : LanguageStatData(language, categoryStats) {
    val streak: Int = streakMap.size
}

class StatisticsViewModel(val repository: Repository) : ViewModel() {
    val activitiesFromBeginning = LiveRealmResults<Activity>(null)
    val activities = LiveRealmResults<Activity>(null)

    val range = MutableLiveData(StatisticRange.MONTH)
    val rangeIndex = Transformations.map(range) { it.index }
    val day = MutableLiveData(LocalDate.now())
    val month = MutableLiveData(YearMonth.now())

    val stats = Transformations.map(activities) { it?.let { it1 -> mapToStats(it1) } }
    var maxMinutes = Transformations.map(stats) { if(!it.isNullOrEmpty()) it.maxOf { it1 -> it1.maxMinutes } else 0L }
    var maxCount = Transformations.map(stats) { if(!it.isNullOrEmpty()) it.maxOf { it1 -> it1.maxCount } else 0 }
    val dayStreakData = Transformations.map(activitiesFromBeginning) { it?.let { it1 -> mapToStreakData(it1, day.value) } }

    init {
        setMonth(YearMonth.now())
    }

    fun setRangeIndex(idx: Int) {
        when (idx) {
            0 -> setDay(day.value!!)
            1 -> setMonth(month.value!!)
            2 -> setAllTime()
        }
    }

    fun setDay(date: LocalDate) {
        range.value = StatisticRange.DAY
        day.value = date
        activities.reset(repository.getActivities(date))
        activitiesFromBeginning.reset(repository.getActivitiesFromBeginningTo(date))
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

    private fun groupByLanguage(items: List<Activity>?): Map<String, List<Activity>> {
        return items?.groupBy { it -> it.language }.orEmpty()
    }

    private fun groupByCategory(items: List<Activity>?): Map<ActivityCategory?, List<Activity>> {
        return items?.groupBy { it -> it.type?.category }.orEmpty()
    }

    private fun mapToStats(items: List<Activity>): List<LanguageStatData>? {
        return groupByLanguage(items).map { languageGroup ->
            val byCategory = groupByCategory(languageGroup.value)
            val typeStats = ActivityCategory.values().map {
                if(byCategory.containsKey(it)) {
                    ActivityCategoryStat(it, byCategory[it].orEmpty())
                } else {
                    ActivityCategoryStat(it, emptyList())
                }
            }
            val groupByDay = items.groupBy { it.date }
            LanguageStatData(languageGroup.key,
                typeStats,
                groupByDay.values.maxOf { it.sumOf { it1 -> getMinutes(it1) } },
                groupByDay.maxOf { it.value.size })
        }.sortedBy { it.language }
    }

    private fun streakFromDate(items: List<Activity>, date: LocalDate): Map<LocalDate,List<Activity>> {
        var lastDate = date
        var streakDays = mutableListOf<LocalDate>()
        val groupedByDay = items.groupBy { it.date }
        val days = groupedByDay.keys.sortedDescending()
        if(days.firstOrNull() != date)
            return emptyMap()

        for(day in days) {
            if(day == date || lastDate == day.plusDays(1)) {
                lastDate = day
                streakDays.add(day)
            } else {
                break
            }
        }
        return groupedByDay.filterKeys { streakDays.contains(it) }
    }

    private fun mapToStreakData(items: List<Activity>, date: LocalDate?): List<DayLanguageStreakData>? {
        if (date == null)
            return emptyList()
        val availableLanguages = groupByLanguage(items.filter { it.date == date }).orEmpty().keys
        return groupByLanguage(items)
            .orEmpty()
            .filter { availableLanguages.contains(it.key) }
            .map { languageGroup ->
                val streak = streakFromDate(languageGroup.value, date)
                val streakActivities = streak.values.flatten()
                val byCategory = groupByCategory(streakActivities)
                val categoryStats = byCategory.orEmpty().map {
                    ActivityCategoryStat(it.key, it.value)
                }
                DayLanguageStreakData(languageGroup.key, categoryStats, streak)
            }
            .sortedBy { it.language }
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