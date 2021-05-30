package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.PickerProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.*
import com.teraculus.lingojournalandroid.utils.mutation
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import java.time.LocalDate


class EditGoalViewModel(
    private val goalId: String?,
    val repository: Repository = Repository.getRepository(),
    val picker: PickerProvider = PickerProvider.getPickerProvider(),
) : ViewModel() {
    private val goal: MutableLiveData<ActivityGoal>
    val preferences = repository.getUserPreferences()
    val types = repository.getTypes()
    val groupedTypes = Transformations.map(types) { it.orEmpty().groupBy { it1 -> it1.category } }

    init {
        val found = if(goalId.isNullOrEmpty()) null else repository.getActivityGoal(goalId.toString()).value
        goal = if(found != null) {
            MutableLiveData(repository.realm?.copyFromRealm(found))
        } else {
            MutableLiveData(ActivityGoal(
                text = "",
                language = preferences.value?.languages?.firstOrNull() ?: "en",
                activityType = types.value!!.first(),
                weekDays = arrayOf(1, 2, 3, 4, 5, 6, 7)))
        }
    }

    val language = Transformations.map(goal) { it.language }
    val type = Transformations.map(goal) { it.type }
    val activityType = Transformations.map(goal) { it.activityType }
    val startDate = Transformations.map(goal) { it.date }
    val endDate = Transformations.map(goal) { it.endDate }
    val effortUnit = Transformations.map(goal) { it.effortUnit }
    val durationGoal = Transformations.map(goal) { it.durationGoal }
    val hoursGoal = Transformations.map(durationGoal) { it?.div(60) }
    val minutesGoal = Transformations.map(durationGoal) { it?.rem(60) }
    val unitCountGoal = Transformations.map(goal) { it.unitCountGoal }
    val weekDays = Transformations.map(goal) { it.weekDays.toIntArray() }

    fun setLanguage(language: String) {
        goal.mutation {
            it.language = language
        }
    }

    fun setGoalType(type: GoalType) {
        goal.mutation {
            it.type = type
        }
    }

    fun addActivityType(it: ActivityType) {
        repository.addActivityType(it)
    }

    fun setActivityType(type: ActivityType) {
        goal.mutation {
            it.activityType = type
            // reset effort type to Time if the measurement unit is also Time
            if(effortUnit.value == EffortUnit.Unit && type.unit == MeasurementUnit.Time) {
                it.effortUnit = EffortUnit.Time
            }
        }
    }

    fun setEffortUnit(unit: EffortUnit) {
        goal.mutation {
            it.effortUnit = unit
        }
    }

    fun setDurationGoal(hours: Int?, minutes: Int?) {
        goal.mutation {
            it.durationGoal = (hours ?: 0) * 60 + (minutes ?: 0)
        }
    }

    fun setUnitCountGoal(count: Float) {
        goal.mutation {
            it.unitCountGoal = count
        }
    }

    fun toggleWeekDay(day: Int) {
        goal.mutation { g ->
            if (g.weekDays.contains(day)) {
                g.weekDays.removeIf { it == day }
            } else {
                g.weekDays.add(day)
            }
        }
    }

    fun pickStartDate() {
        picker.pickDate("Start date", initialDate = startDate.value?: LocalDate.now()) { newDate ->
            goal.mutation { g ->
                g.let { gv ->
                    gv.endDate?.let { ed ->
                        if (ed < newDate) {
                            gv.endDate = newDate.plusDays(1)
                        }
                    }

                    gv.date = newDate
                }
            }
        }
    }

    fun pickEndDate() {
        picker.pickDate("End date", initialDate = endDate.value?: LocalDate.now()) { newDate ->
            goal.mutation { g ->
                g.let { gv ->
                    if (gv.date > newDate) {
                        gv.date = newDate.minusDays(1)
                    }

                    gv.endDate = newDate
                }
            }
        }
    }

    fun save() {
        // because I need to make sure the focus is cleared from the number input
        viewModelScope.launch {
            goal.value?.let {
                repository.insertOrUpdateActivityGoal(it)
            }
        }
    }

    fun delete() {
        goalId?.let { repository.removeActivityGoal(ObjectId(it)) }
    }

}

class EditGoalViewModelFactory(val goalId: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditGoalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditGoalViewModel(goalId) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}