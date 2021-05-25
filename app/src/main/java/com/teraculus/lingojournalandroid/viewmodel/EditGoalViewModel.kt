package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teraculus.lingojournalandroid.PickerProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ActivityGoal
import com.teraculus.lingojournalandroid.model.ActivityType
import com.teraculus.lingojournalandroid.model.EffortUnit
import com.teraculus.lingojournalandroid.model.GoalType
import com.teraculus.lingojournalandroid.utils.mutation
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
        }
    }

    fun setEffortUnit(unit: EffortUnit) {
        goal.mutation {
            it.effortUnit = unit
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
        goal.value?.let {
            repository.insertOrUpdateActivityGoal(it)
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