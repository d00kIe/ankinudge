package com.teraculus.lingojournalandroid.viewmodel

import android.util.Range
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.*
import io.realm.RealmResults
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

// Daily - I want to see for a certain atDate the progress of each daily goal
// Provides goals for a certain date and language
class GoalsOnDateViewModel(repository: Repository, date: LocalDate, val language: String) : ViewModel() {
    private val goals = repository.goals.all()
    private val frozenGoals = Transformations.map(goals) { (it as RealmResults<ActivityGoal>).freeze().sortedByDescending { g -> g.id.timestamp } }
    val todayGoals = Transformations.map(frozenGoals) { it.filter { g -> goalFilter(g, date) } }
    val hasGoals = Transformations.map(todayGoals) { it.isNotEmpty() }
    val dailyGoals = Transformations.map(todayGoals) { it.filter { g -> g.type == GoalType.Daily } }
    val longTermGoals = Transformations.map(todayGoals) { it.filter { g -> g.type == GoalType.LongTerm } }

    private fun goalFilter(
        g: ActivityGoal,
        date: LocalDate,
    ) = if (g.type == GoalType.Daily)
        g.active && g.language == language && g.weekDays.contains(date.dayOfWeek.value)
    else
        g.active && g.language == language && g.date <= date && (g.endDate == null || g.endDate!! >= date)

    class Factory(private val day: LocalDate = LocalDate.now(), private val language: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GoalsOnDateViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GoalsOnDateViewModel(Repository.getRepository(), day, language) as T
            }

            throw IllegalArgumentException("Unknown view model class")
        }
    }
}

// Calculates goal progress at a certain date
class GoalProgressOnDateViewModel(
    private val frozenGoal: ActivityGoal,
    private val atDate: LocalDate,
    private val owner: LifecycleOwner,
    val repository: Repository = Repository.getRepository(),
) : ViewModel() {
    private val goal = Repository.getRepository().goals.get(frozenGoal.id.toString())
    val snapshot =
        MutableLiveData<ActivityGoal>(if (goal.value?.isValid == true) goal.value!!.freeze<ActivityGoal>() else null)

    val activities = Transformations.switchMap(snapshot) {
        it?.let { g ->
            if(g.type == GoalType.Daily) {
                return@switchMap LiveRealmResults(repository.activities.all(atDate))
            } else {
                return@switchMap LiveRealmResults(repository.activities.all(g.date, atDate))
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

    init {
        goal.observe(
            owner,
            Observer {
                snapshot.value = if (it?.isValid == true) it.freeze() else null
            }
        )
    }

    private fun match(activity: Activity, goal: ActivityGoal) : Boolean {
        with(activity)
        {
            return language == goal.language && type?.id == goal.activityType?.id
        }
    }

    class Factory(
        private val rawGoal: ActivityGoal,
        val day: LocalDate = LocalDate.now(),
        private val owner: LifecycleOwner,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GoalProgressOnDateViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GoalProgressOnDateViewModel(rawGoal, day, owner) as T
            }

            throw IllegalArgumentException("Unknown view model class")
        }
    }
}


// Calculates composite goal progress at a certain date range
class RangeGoalProgressViewModel(
    private val range: Range<LocalDate>,
    private val goalId: String,
    val repository: Repository = Repository.getRepository(),
) : ViewModel() {
    private val _goal = repository.goals.get(goalId)
    private val _frozen = Transformations.map(_goal) { it?.freeze<ActivityGoal>() }
    private val yearMonthRange = Range.create(YearMonth.of(range.lower.year, range.lower.month), YearMonth.of(range.upper.year, range.upper.month))
    private val activities = Transformations.switchMap(_frozen) { g -> repository.activities.allLive(range.lower, range.upper, g?.language) }
    private val frozenActivities = Transformations.map(activities) { (it as RealmResults<Activity>).freeze() }
    private val perDayActivities = Transformations.map(frozenActivities) { it?.filter { a -> match(a, _frozen.value) }.orEmpty().groupBy { a -> a.date } }
    private val perMonthActivities = Transformations.map(frozenActivities) { it?.filter { a -> match(a, _frozen.value) }.orEmpty().groupBy { a -> YearMonth.of(a.date.year, a.date.month) } }
    val perDayGoals = Transformations.map(perDayActivities) { it.orEmpty().filterKeys { d -> range.contains(d) }.mapValues { entry -> getProgress(_frozen.value, entry.value); }.toSortedMap() }
    val perMonthGoals = Transformations.map(perMonthActivities) { it.orEmpty().filterKeys { m -> yearMonthRange.contains(m) }.mapValues { entry -> getProgress(_frozen.value, entry.value); }.toSortedMap() }

    private fun getProgress(goal: ActivityGoal?, activities: List<Activity>) : Float {
        if (goal == null)
            return 0f

        return if(goal.effortUnit == EffortUnit.Time) {
            val progress = activities.sumOf { a -> a.duration }.toFloat()
            (100.0 / (goal.durationGoal?.toFloat() ?: 1f) * progress).toFloat()
        } else {
            val progress = activities.sumOf { a -> a.unitCount.toDouble() }.toFloat()
            (100.0 / (goal.unitCountGoal ?: 1f) * progress).toFloat()
        }
    }

    private fun match(activity: Activity, goal: ActivityGoal?) : Boolean = with(activity)
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
    private val range: Range<LocalDate>,
    private val goalId: String,
    val repository: Repository = Repository.getRepository(),
) : ViewModel() {
    private val _goal = repository.goals.get(goalId)
    private val _frozen = Transformations.map(_goal) { it?.freeze<ActivityGoal>() }
    private val yearMonthRange = Range.create(YearMonth.of(range.lower.year, range.lower.month), YearMonth.of(range.upper.year, range.upper.month))
    private val activities = Transformations.switchMap(_frozen) {
            g -> g?.date?.let {
                val intersection = range.intersect(it, range.upper)
                repository.activities.allLive(intersection.lower, intersection.upper, g?.language)
            }
    }
    private val frozenActivities = Transformations.map(activities) { (it as RealmResults<Activity>?)?.freeze() }
    private val perDayActivities = Transformations.map(frozenActivities) { it?.filter { a -> match(a, _frozen.value) }.orEmpty().groupBy { a -> a.date } }
    private val perMonthActivities = Transformations.map(frozenActivities) { it?.filter { a -> match(a, _frozen.value) }.orEmpty().groupBy { a -> YearMonth.of(a.date.year, a.date.month) } }
    val perDayGoals: LiveData<Map<LocalDate, Float>> = Transformations.map(perDayActivities) { it ->
        var acc = 0f;
        val realRange = range.intersect(_frozen.value?.date, range.upper)
        val res = it.orEmpty()
            .toSortedMap()
            .mapValues { entry -> acc += getProgress(_frozen.value, entry.value); acc }
            .filterKeys { d -> realRange?.contains(d) ?: false }

        // add
        val dayBeforeStartOfGoal = _frozen.value?.date?.minusDays(1)
        if(range.contains(dayBeforeStartOfGoal) && !res.containsKey(dayBeforeStartOfGoal)) {
            val newRes = res.toMutableMap()
            newRes[dayBeforeStartOfGoal] = 0f
            return@map newRes.toSortedMap().toMap()
        } else {
            return@map res
        }
    }

    val perMonthGoals = Transformations.map(perMonthActivities) { var acc = 0f; it.orEmpty().toSortedMap().mapValues { entry -> acc += getProgress(_goal.value, entry.value); acc }.filterKeys { m -> yearMonthRange.contains(m) } }

    private fun getProgress(goal: ActivityGoal?, activities: List<Activity>) : Float {
        if (goal == null)
            return 0f

        return if(goal.effortUnit == EffortUnit.Time) {
            val progress = activities.sumOf { a -> a.duration }.toFloat()
            (100.0 / (goal.durationGoal?.toFloat() ?: 1f) * progress).toFloat()
        } else {
            val progress = activities.sumOf { a -> a.unitCount.toDouble() }.toFloat()
            (100.0 / (goal.unitCountGoal ?: 1f) * progress).toFloat()
        }
    }

    private fun match(activity: Activity, goal: ActivityGoal?) : Boolean = with(activity)
    {
        return goal?.let { language == it.language && type?.id == it.activityType?.id } ?: false
    }


    class Factory(
        private val range: Range<LocalDate>,
        private val goalId: String,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AccumulatingRangeGoalProgressViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AccumulatingRangeGoalProgressViewModel(range, goalId) as T
            }

            throw IllegalArgumentException("Unknown view model class")
        }
    }
}