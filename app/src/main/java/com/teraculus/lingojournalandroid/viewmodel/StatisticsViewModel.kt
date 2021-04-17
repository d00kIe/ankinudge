package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityCategory
import com.teraculus.lingojournalandroid.model.LiveRealmResults
import com.teraculus.lingojournalandroid.utils.getMinutes
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil


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

class DayLanguageStreakData (
    language: String,
    categoryStats: List<ActivityCategoryStat>,
    streakMap:  Map<LocalDate, List<Activity>>
) : LanguageStatData(language, categoryStats) {
    val streak: Int = streakMap.size
}

data class DayData(
    val day: Int,
    val month: Int,
    val year: Int,
    val thisMonth: Boolean,
    val today: Boolean,
    var hasActivities: Boolean,
    val minutes: Long,
    val count: Int,
    val maxMinutes: Long?,
    val maxCount: Int?,
) {
    override fun toString() : String {
        return "${day}: ${month}: $year"
    }
}

fun getMonthDayData(month: Int, year: Int, activities: List<Activity>?): List<DayData> {
    val dataItems = ArrayList<DayData>()
    val today = LocalDate.now()
    val firstDayOfMonth: LocalDate = LocalDate.of(year, month, 1)
    val lastDatOfPrevMonth = firstDayOfMonth.minusDays(1)
    val firstDayOfNextMonth: LocalDate = firstDayOfMonth.plusMonths(1)
    val dayOfWeek = DayOfWeek.from(firstDayOfMonth)
    for (i in 1 until dayOfWeek.value) {
        dataItems.add(DayData(
            0,
            lastDatOfPrevMonth.monthValue,
            lastDatOfPrevMonth.year,
            thisMonth = false,
            today = false,
            hasActivities = false,
            minutes = 0,
            count = 0,
            maxMinutes = null,
            maxCount = null))
    }

    val thisMonth = today.monthValue == month && today.year == year
    val groupedByDate = activities.orEmpty().groupBy { it.date }
    val groupedByDateMinutes = groupedByDate.mapValues { it.value.sumOf { it1 -> getMinutes(it1) } }
    val maxMinutes =
        groupedByDateMinutes.values.maxOrNull()
    val maxCount =
        groupedByDate.mapValues { it.value.size }.values.maxOrNull()

    for (i in 1..firstDayOfMonth.lengthOfMonth()) {
        val date = LocalDate.of(year, month, i)
        if(groupedByDate.containsKey(date)) {
            val dayActivities = groupedByDate[date]
            dataItems.add(DayData(i,
                month,
                year,
                thisMonth = true,
                today = thisMonth && today.dayOfMonth == i,
                hasActivities = !dayActivities.isNullOrEmpty(),
                minutes = groupedByDateMinutes[date] ?: 0L,
                count = dayActivities.orEmpty().size,
                maxMinutes = maxMinutes,
                maxCount = maxCount))
        } else {
            dataItems.add(DayData(i,
                month,
                year,
                thisMonth = true,
                today = thisMonth && today.dayOfMonth == i,
                hasActivities = false,
                minutes = 0,
                count = 0,
                maxMinutes = maxMinutes,
                maxCount = maxCount))
        }
    }


    val trailingDayCount = ceil(dataItems.size / 7.0).toInt() * 7 - dataItems.size

    for (i in 1..trailingDayCount) {
        dataItems.add(DayData(0,
            firstDayOfNextMonth.monthValue,
            firstDayOfNextMonth.year,
            thisMonth = false,
            today = false,
            hasActivities = false,
            minutes = 0,
            count = 0,
            maxMinutes = null,
            maxCount = null))
    }

    return dataItems
}

class MonthItemViewModel(val repository: Repository = Repository.getRepository(), yearMonth: YearMonth) : ViewModel() {
    private val activities = LiveRealmResults<Activity>(null)
    val daydata = MutableLiveData(getMonthDayData(yearMonth.monthValue, yearMonth.year, emptyList()))
    init {
        val from = LocalDate.of(yearMonth.year, yearMonth.month, 1)
        val to = from.withDayOfMonth(yearMonth.lengthOfMonth())
        activities.reset(repository.getActivities(from, to))
        val frozenActivities = activities.value.orEmpty().map { it1 -> it1.freeze<Activity>() }
        daydata.value =getMonthDayData(yearMonth.monthValue, yearMonth.year, frozenActivities)
        activities.observeForever {
            val observedFrozenActivities = it.orEmpty().map { it1 -> it1.freeze<Activity>() }
            viewModelScope.launch {
                daydata.postValue(getMonthDayData(yearMonth.monthValue, yearMonth.year, observedFrozenActivities))
            }
        }
    }
}

class MonthItemViewModelFactory(private val yearMonth: YearMonth) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MonthItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MonthItemViewModel(Repository.getRepository(), yearMonth) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}

class StatisticsViewModel(val repository: Repository) : ViewModel() {
    private val activitiesFromBeginning = LiveRealmResults<Activity>(null)
    val activities = LiveRealmResults<Activity>(null)

    val range = MutableLiveData(StatisticRange.MONTH)
    val rangeIndex = Transformations.map(range) { it.index }
    val day = MutableLiveData(LocalDate.now())
    val month = MutableLiveData(YearMonth.now())

    val stats = Transformations.map(activities) { it?.let { it1 -> mapToStats(it1) } } //
    val dayStreakData = Transformations.map(activitiesFromBeginning) { it?.let { it1 -> mapToStreakData(it1, day.value) } }
//    val stats = MutableLiveData<List<LanguageStatData>?>(emptyList()) //Transformations.map(activities) { it?.let { it1 -> mapToStats(it1) } } //
//    val dayStreakData = MutableLiveData<List<DayLanguageStreakData>?>(emptyList())//Transformations.map(activitiesFromBeginning) { it?.let { it1 -> mapToStreakData(it1, day.value) } }
    init {
        //TODO: Try How to run LiveData transformations on a coroutine https://gist.github.com/luciofm/cc463f38f488c4c4fccf531b53c6ac10
//        activities.observeForever {
//            val frozenActivities = it.orEmpty().map { it1 -> it1.freeze<Activity>() }
//            viewModelScope.launch {
//                stats.postValue(mapToStats(frozenActivities))
//            }
//        }
//
//        activitiesFromBeginning.observeForever {
//            val frozenActivities = it.orEmpty().map { it1 -> it1.freeze<Activity>() }
//            viewModelScope.launch {
//                dayStreakData.postValue(mapToStreakData(frozenActivities, day.value))
//            }
//        }
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
}

private fun groupByLanguage(items: List<Activity>?): Map<String, List<Activity>> {
    return items?.groupBy { it -> it.language }.orEmpty()
}

private fun groupByCategory(items: List<Activity>?): Map<ActivityCategory?, List<Activity>> {
    return items?.groupBy { it -> it.type?.category }.orEmpty()
}

private fun mapToStats(items: List<Activity>): List<LanguageStatData> {
    return groupByLanguage(items).map { languageGroup ->
        val byCategory = groupByCategory(languageGroup.value)
        val typeStats = ActivityCategory.values().map {
            if(byCategory.containsKey(it)) {
                ActivityCategoryStat(it, byCategory[it].orEmpty())
            } else {
                ActivityCategoryStat(it, emptyList())
            }
        }
        LanguageStatData(languageGroup.key, typeStats)
    }.sortedBy { it.language }
}

fun streakFromDate(items: List<Activity>, date: LocalDate, relaxed: Boolean = false): Map<LocalDate,List<Activity>> {
    var lastDate = date
    val inStreak = mutableListOf<Activity>()
    for (a in items.sortedByDescending { it.date }) {
        if((relaxed && a.date == date.minusDays(1))
            || date == a.date
            || lastDate == a.date
            || lastDate == a.date.plusDays(1))
        {
            lastDate = a.date
            inStreak.add(a)
        } else {
            break
        }
    }

    return inStreak.groupBy { it.date }
}

fun mapToStreakData(items: List<Activity>, date: LocalDate?): List<DayLanguageStreakData> {
    if (date == null)
        return emptyList()
    val availableLanguages = groupByLanguage(items.filter { it.date == date }).keys
    return groupByLanguage(items)
        .filter { availableLanguages.contains(it.key) }
        .map { languageGroup ->
            val streak = streakFromDate(languageGroup.value, date)
            val streakActivities = streak.values.flatten()
            val byCategory = groupByCategory(streakActivities)
            val categoryStats = byCategory.map {
                ActivityCategoryStat(it.key, it.value)
            }
            DayLanguageStreakData(languageGroup.key, categoryStats, streak)
        }
        .sortedBy { it.language }
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