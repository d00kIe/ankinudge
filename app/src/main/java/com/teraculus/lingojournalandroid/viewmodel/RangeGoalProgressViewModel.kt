package com.teraculus.lingojournalandroid.viewmodel

import android.util.Range
import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.*
import com.teraculus.lingojournalandroid.utils.DateProgression
import com.teraculus.lingojournalandroid.utils.asLocalDate
import io.realm.RealmResults
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)
// Calculates composite goal progress at a certain date range
class RangeGoalProgressViewModel(
    private val range: Range<LocalDate>,
    private val goalId: String,
    val repository: Repository = Repository.getRepository(),
) : ViewModel() {
    private val _goal = repository.goals.get(goalId)
    private val _frozen = Transformations.map(_goal) { it?.freeze<ActivityGoal>() }
    private val activities = Transformations.switchMap(_frozen) { g ->
        g?.date?.let { startDate ->
            val endDate = if(!g.active) asLocalDate(g.lastActiveChange) else range.upper
            val intersection = range.intersect(startDate, endDate)
            repository.activities.allLive(intersection.lower, intersection.upper, g.language)
        }
    }

    private val frozenActivities =
        Transformations.map(activities) { (it as RealmResults<Activity>).freeze() }

    private val perDayActivities = Transformations.map(frozenActivities) {
        it?.filter { a -> match(a, _frozen.value) }.orEmpty().groupBy { a -> a.date }
    }

    val perDayGoals = Transformations.map(perDayActivities) {
        it.orEmpty().filterKeys { d -> range.contains(d) }
            .mapValues { entry -> getProgress(_frozen.value, entry.value); }.toSortedMap()
    }

    private fun getProgress(goal: ActivityGoal?, activities: List<Activity>): Float {
        if (goal == null)
            return 0f

        return if (goal.effortUnit == EffortUnit.Time) {
            val progress = activities.sumOf { a -> a.duration }.toFloat()
            (100.0 / (goal.durationGoal?.toFloat() ?: 1f) * progress).toFloat()
        } else {
            val progress = activities.sumOf { a -> a.unitCount.toDouble() }.toFloat()
            (100.0 / (goal.unitCountGoal ?: 1f) * progress).toFloat()
        }
    }

    private fun match(activity: Activity, goal: ActivityGoal?): Boolean = with(activity)
    {
        return goal?.let { language == it.language && type?.id == it.activityType?.id } ?: false
    }

    class Factory(
        private val range: Range<LocalDate>,
        private val goalId: String,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RangeGoalProgressViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RangeGoalProgressViewModel(range, goalId) as T
            }

            throw IllegalArgumentException("Unknown view model class")
        }
    }
}

// Calculates composite goal progress at a certain date range
class AccumulatingRangeGoalProgressViewModel(
    private val goalId: String,
    val repository: Repository = Repository.getRepository(),
) : ViewModel() {
    private val _goal = repository.goals.get(goalId)
    private val _frozenGoal = Transformations.map(_goal) { it?.freeze<ActivityGoal>() }

    val chartRange = Transformations.map(_frozenGoal) { goal ->
        goal?.let {
            if (goal.active)
                Range.create(LocalDate.now().minusDays(29), LocalDate.now())
            else
                Range.create(asLocalDate(goal.lastActiveChange).minusDays(29), asLocalDate(goal.lastActiveChange))
        }
    }

    val dataRange = Transformations.map(_frozenGoal) { goal ->
        goal?.let {
            if (goal.active)
                Range.create(goal.date, goal.endDate ?: LocalDate.now())
            else
                Range.create(goal.date, asLocalDate(goal.lastActiveChange))
        }
    }

    private val activities = Transformations.switchMap(dataRange) { dr ->
        dr?.let {
            repository.activities.allLive(dr.lower, dr.upper, _frozenGoal.value?.language)
        }
    }

    private val frozenActivities =
        Transformations.map(activities) { (it as RealmResults<Activity>?)?.freeze() }

    private val perDayActivities = Transformations.map(frozenActivities) {
        it?.filter { a -> match(a, _frozenGoal.value) }.orEmpty().groupBy { a -> a.date }
    }

    val perDayGoals: LiveData<SortedMap<LocalDate, Float>> = Transformations.map(perDayActivities) { it ->
        var acc = 0f;
        val res = it.orEmpty()
            .toSortedMap()
            .mapValues { entry -> acc += getProgress(_frozenGoal.value, entry.value); acc }
            .toSortedMap()
            //.filterKeys { d -> dataRange.value?.contains(d) ?: false }

        // add
        val dayBeforeStartOfGoal = _frozenGoal.value?.date?.minusDays(1)
        if (chartRange.value?.contains(dayBeforeStartOfGoal) == true && !res.containsKey(dayBeforeStartOfGoal)) {
            val newRes = res.toMutableMap()
            newRes[dayBeforeStartOfGoal] = 0f
            return@map newRes.toSortedMap()
        } else {
            return@map res
        }
    }

    var perDayChartMap = Transformations.map(perDayGoals) { map ->
        // fill the map with prevValue if no value available for that day
        val res =  map.toMutableMap()

        chartRange.value?.let { r ->
            val leftSideOfChart = map.headMap(r.lower)
            var prevValue: Float? = if(leftSideOfChart.isEmpty()) 0f else leftSideOfChart[leftSideOfChart.lastKey()]
            for (day in r.lower..r.upper) {
                if(res.containsKey(day)) {
                    prevValue = res[day]
                } else {
                    res[day] = prevValue ?: 0f
                }
            }
        }
        res.mapKeys { ChronoUnit.DAYS.between(chartRange.value?.lower, it.key).toInt() }
    }

    private fun getProgress(goal: ActivityGoal?, activities: List<Activity>): Float {
        if (goal == null)
            return 0f

        return if (goal.effortUnit == EffortUnit.Time) {
            val progress = activities.sumOf { a -> a.duration }.toFloat()
            (100.0 / (goal.durationGoal?.toFloat() ?: 1f) * progress).toFloat()
        } else {
            val progress = activities.sumOf { a -> a.unitCount.toDouble() }.toFloat()
            (100.0 / (goal.unitCountGoal ?: 1f) * progress).toFloat()
        }
    }

    private fun match(activity: Activity, goal: ActivityGoal?): Boolean = with(activity)
    {
        return goal?.let { language == it.language && type?.id == it.activityType?.id } ?: false
    }


    class Factory(private val goalId: String,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AccumulatingRangeGoalProgressViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AccumulatingRangeGoalProgressViewModel(goalId) as T
            }

            throw IllegalArgumentException("Unknown view model class")
        }
    }
}