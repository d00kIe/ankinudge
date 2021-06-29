package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityCategory
import com.teraculus.lingojournalandroid.model.LiveRealmResults
import com.teraculus.lingojournalandroid.model.transform
import io.realm.RealmResults
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import kotlin.math.ceil


// Ideas:
// Language split
// Activity types split per category
// Compared to previous day/month
// Achieved level : Add additional button to add achieved language competency
// Streak per activity category
// Average daily tasks
enum class StatisticRange(val title: String, val index: Int) {
    DAY("Day", 0),
    MONTH("Month", 1),
    YEAR("Year", 2)
}

class ActivityCategoryStat(val category: ActivityCategory?, activities: List<Activity>) {
    val minutes: Int = activities.map { it.duration }.sum()
    val count: Int = activities.size
    val confidence: Float = activities.map { it.confidence }.average().toFloat()
    val motivation: Float = activities.map { it.motivation }.average().toFloat()
    val topTypes = activities.groupBy { it.type }.map { Pair(it.key, it.value.sumOf { a -> a.duration }) }.sortedBy { it.second }
}

open class LanguageStatData(
    val language: String,
    val categoryStats: List<ActivityCategoryStat>
) {
    val allMinutes: Int =
        if (categoryStats.isNotEmpty()) categoryStats.map { it.minutes }.sum() else 0
    val allCount: Int =
        if (categoryStats.isNotEmpty()) categoryStats.map { it.count }.sum() else 0
    val allConfidence: Float =
        if (categoryStats.isNotEmpty()) categoryStats.filter { !it.confidence.isNaN() }.map { it.confidence }.average()
            .toFloat() else 0f
    val allMotivation: Float =
        if (categoryStats.isNotEmpty()) categoryStats.filter { !it.motivation.isNaN() }.map { it.motivation }.average()
            .toFloat() else 0f
    val topActivityTypes = categoryStats.flatMap { it.topTypes }.sortedByDescending { it.second }.take(5)
    val topActivityTypeMinutes = topActivityTypes.firstOrNull()?.second ?: 0

    companion object {
        fun empty(): LanguageStatData {
            return LanguageStatData("",
                listOf())
        }
    }
}

class DayLanguageStreakData (
    language: String,
    categoryStats: List<ActivityCategoryStat>,
    streakMap:  Map<LocalDate, List<Activity>>
) : LanguageStatData(language,
    categoryStats) {
    val streak: Int = streakMap.size
}

data class DayData(
    val day: Int,
    val month: Int,
    val year: Int,
    val thisMonth: Boolean,
    val today: Boolean,
    var hasActivities: Boolean,
    val minutes: Int,
    val count: Int,
    val maxMinutes: Int?,
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
    val groupedByDateMinutes = groupedByDate.mapValues { it.value.sumOf { it1 -> it1.duration } }
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
                minutes = groupedByDateMinutes[date] ?: 0,
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
    val frozenActivities = Transformations.map(activities) { if(it != null) (it as RealmResults<Activity>).freeze() else null }
    val daydata = frozenActivities.transform(scope = viewModelScope) { getMonthDayData(yearMonth.monthValue, yearMonth.year, it.orEmpty()) }
    init {
        val from = LocalDate.of(yearMonth.year, yearMonth.month, 1)
        val to = from.withDayOfMonth(yearMonth.lengthOfMonth())
        activities.reset(repository.activities.all(from, to))
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
    val frozenActivitiesFromBeginning = Transformations.map(activitiesFromBeginning) { if(it != null) (it as RealmResults<Activity>).freeze() else null }
    val activities = LiveRealmResults<Activity>(null)
    val frozenActivities = Transformations.map(activities) { if(it != null) (it as RealmResults<Activity>).freeze() else null }
    val loading = Transformations.map(activities) { it == null }

    val range = MutableLiveData(StatisticRange.MONTH)
    val rangeIndex = Transformations.map(range) { it.index }
    val day = MutableLiveData(LocalDate.now())
    val month = MutableLiveData(YearMonth.now())
    val year = MutableLiveData(Year.now())

    val stats = frozenActivities.transform(scope = viewModelScope) {
        if(it != null) {
            mapToStats(it)
        }
        else
            emptyList()
    } //
    val dayStreakData = frozenActivitiesFromBeginning.transform(scope = viewModelScope) { if(it != null) mapToStreakData(it, day.value) else emptyList() }
    private var language = MutableLiveData("") //only for internal use, could be different than the selected language
    val languages = Transformations.map(stats) { it.map { d -> d.language } }
    val languageIndex = MediatorLiveData<Int>().apply {
        fun update() {
            value = if (languages.value?.contains(language.value) == true) languages.value?.indexOf(language.value) else 0
        }

        addSource(language) { update() }
        addSource(languages) { update() }

        update()
    }

    val selectedLanguage = MediatorLiveData<String>().apply {
        fun update() {
            value = languageIndex.value?.let { languages.value?.getOrElse(it, { "" }) }
        }

        addSource(languageIndex) { update() }
        addSource(languages) { update() }

        update()
    }

    val languageStats = Transformations.map(languageIndex) { langIdx ->
        if(langIdx >= stats.value.orEmpty().size) {
            LanguageStatData.empty()
        } else {
            stats.value.orEmpty()[langIdx]
        }
    }

    val languageDayStreak = MediatorLiveData<DayLanguageStreakData>().apply {
        fun update() {
            value = if(languageIndex.value == null || languageIndex.value!! >= dayStreakData.value.orEmpty().size) {
                null
            } else {
                dayStreakData.value.orEmpty()[languageIndex.value!!]
            }
        }

        addSource(languageIndex) { update() }
        addSource(dayStreakData) { update() }

        update()
    }

    init {
        setMonth(YearMonth.now())
    }

    fun setLanguage(newLanguage: String) {
        language.value = newLanguage
    }

    fun setRangeIndex(idx: Int) {
        when (idx) {
            0 -> setDay(day.value!!)
            1 -> setMonth(month.value!!)
            2 -> setYear(year.value!!)
        }
    }

    fun setDay(date: LocalDate) {
        range.value = StatisticRange.DAY
        day.value = date
        activities.reset(repository.activities.all(date))
        activitiesFromBeginning.reset(repository.activities.allUntil(date))
    }

    fun setMonth(yearMonth: YearMonth) {
        range.value = StatisticRange.MONTH
        month.value = yearMonth
        val from = LocalDate.of(yearMonth.year, yearMonth.month, 1)
        val to = from.withDayOfMonth(yearMonth.lengthOfMonth())
        activities.reset(repository.activities.all(from, to))
    }

    fun setYear(yearVal: Year) {
        range.value = StatisticRange.YEAR
        year.value = yearVal
        val from = LocalDate.of(yearVal.value, 1, 1)
        val to = LocalDate.of(yearVal.value, 12, 31)
        activities.reset(repository.activities.all(from, to))
    }
}

private fun groupByLanguage(items: List<Activity>?): Map<String, List<Activity>> {
    return items?.groupBy { it -> it.language }.orEmpty()
}

private fun groupByCategory(items: List<Activity>?): Map<ActivityCategory?, List<Activity>> {
    return items?.groupBy { it -> it.type?.category }.orEmpty()
}

private fun groupByDay(items: List<Activity>?): Map<LocalDate, List<Activity>> {
    return items?.groupBy { it -> it.date }.orEmpty()
}

private fun mapToStats(items: List<Activity>): List<LanguageStatData> {
    return groupByLanguage(items).map { languageGroup ->
        // create per-type stats
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