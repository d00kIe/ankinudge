package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teraculus.lingojournalandroid.PickerProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityType
import com.teraculus.lingojournalandroid.utils.getMinutes
import java.time.LocalDate
import java.time.LocalTime

class EditActivityViewModel(
    private val repository: Repository,
    private val pickerProvider: PickerProvider,
    id: String?,
    goalId: String?,
) : ViewModel() {
    val types = repository.getTypes()
    val groupedTypes = Transformations.map(types) { it.orEmpty().groupBy { it1 -> it1.category } }
    private var preparedId: String? = null
    val date = MutableLiveData(LocalDate.now())
    val startTime = MutableLiveData(LocalTime.now().minusHours(1))
    val endTime = MutableLiveData(LocalTime.now())
    val unitCount = MutableLiveData(1.0f)
    val title = MutableLiveData("")
    val text = MutableLiveData("")
    val language = MutableLiveData("")
    val type = MutableLiveData(types.value!!.first())
    val confidence = MutableLiveData(75f)
    val motivation = MutableLiveData(75f)
    val preferences = repository.getUserPreferences()

    init {
        prepareActivity(id, goalId)
    }

    private fun prepareActivity(id: String?, goalId: String?) {
        val activity = id?.let { repository.getActivity(it).value }
        if (activity != null) {
            preparedId = id
            title.value = activity.title
            text.value = activity.text
            language.value = activity.language
            type.value = activity.type
            confidence.value = activity.confidence
            motivation.value = activity.motivation
            date.value = activity.date
            startTime.value = activity.startTime
            endTime.value = activity.endTime
            unitCount.value = activity.unitCount
        } else {
            val goal = goalId?.let { repository.getActivityGoal(it).value }
            if(goal == null) {
                title.value = ""
                text.value = ""
                language.value = preferences.value?.languages?.firstOrNull() ?: "en"
                type.value = types.value!!.first()
                confidence.value = 50f
                motivation.value = 50f
                date.value = LocalDate.now()
                startTime.value = LocalTime.now().minusHours(1)
                endTime.value = LocalTime.now()
                unitCount.value = 1f
            } else {
                title.value = ""
                text.value = ""
                language.value = goal.language
                type.value = types.value!!.find { it.id == goal.activityType?.id } ?: types.value!!.first()
                confidence.value = 50f
                motivation.value = 50f
                date.value = LocalDate.now()
                startTime.value = LocalTime.now().minusHours(1)
                endTime.value = LocalTime.now()
                unitCount.value = 1f
            }
        }
    }

    fun onTitleChange(value: String) {
        if (title.value != value)
            title.value = value
    }

    fun onTextChange(value: String) {
        if (text.value != value)
            text.value = value
    }

    fun onTypeChange(value: ActivityType) {
        if (type.value != value)
            type.value = value
    }

    fun onLanguageChange(value: String) {
        if (language.value != value)
            language.value = value // language code
    }

    fun onUnitCountChange(value: Float) {
        if (unitCount.value != value)
            unitCount.value = value
    }

    fun onConfidenceChange(value: Float) {
        if (confidence.value != value)
            confidence.value = value
    }

    fun onMotivationChange(value: Float) {
        if (motivation.value != value)
            motivation.value = value
    }

    suspend fun pickDate() {
        pickerProvider.pickDate(null, date.value!!) { date.value = it; }
    }

    suspend fun pickStartTime() {
        val minutes = getMinutes(startTime.value!!, endTime.value!!)
        pickerProvider.pickTime("Select start time", startTime.value!!) {
            startTime.value = it
            setEndTimeFromDuration(minutes)
        }
    }

    suspend fun pickDuration() {
        if (startTime.value != null && endTime.value != null) {
            val minutes = getMinutes(startTime.value!!, endTime.value!!)
            val duration = LocalTime.of(0, 0).plusMinutes(minutes)
            pickerProvider.pickDuration("Duration", duration) {
                setEndTimeFromDuration(it.hour * 60L + it.minute)
            }
        }
    }

    private fun setEndTimeFromDuration(minutes: Long) {
        endTime.value = LocalTime.from(startTime.value).plusMinutes(minutes)
    }

    private fun addNote() {
        repository.addActivity(Activity(title.value!!,
            text.value!!,
            language.value!!,
            type.value!!,
            unitCount.value!!,
            confidence.value!!,
            motivation.value!!,
            date.value!!,
            startTime.value!!,
            endTime.value!!))
    }

    private fun updateNote(id: String) {

        repository.updateActivity(id,
            title.value!!,
            text.value!!,
            language.value!!,
            type.value!!,
            unitCount.value!!,
            confidence.value!!,
            motivation.value!!,
            date.value!!,
            startTime.value!!,
            endTime.value!!)
    }

    fun save() {
        if (preparedId == null) {
            addNote()
        } else preparedId?.let {
            updateNote(it)
        }
    }

    fun addActivityType(it: ActivityType) {
        repository.addActivityType(it)
    }
}

class EditActivityViewModelFactory(val id: String?, val goalId: String?, private val pickerProvider: PickerProvider) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditActivityViewModel(Repository.getRepository(), pickerProvider, id, goalId) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}