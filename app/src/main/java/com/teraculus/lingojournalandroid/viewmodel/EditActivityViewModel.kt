package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teraculus.lingojournalandroid.PickerProvider
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.data.getAllLanguages
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityType
import com.teraculus.lingojournalandroid.utils.getMinutes
import java.time.LocalDate
import java.time.LocalTime

class EditActivityViewModel(
    private val repository: Repository,
    private val pickerProvider: PickerProvider,
    id: String?,
) : ViewModel() {
    val types = repository.getTypes()
    private var preparedId: String? = null
    val languages = MutableLiveData(getAllLanguages())
    val date = MutableLiveData(LocalDate.now())
    val startTime = MutableLiveData(LocalTime.now().minusHours(1))
    val endTime = MutableLiveData(LocalTime.now())
    val title = MutableLiveData("")
    val text = MutableLiveData("")
    val language = MutableLiveData("")
    val type = MutableLiveData(types.value!!.first())
    val confidence = MutableLiveData(75f)
    val motivation = MutableLiveData(75f)
    val preferences = repository.getUserPreferences()

    init {
        prepareActivity(id)
    }

    private fun prepareActivity(id: String?) {
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
        } else {
            title.value = ""
            text.value = ""
            language.value = ""
            type.value = types.value!!.first()
            confidence.value = 50f
            motivation.value = 50f
            date.value = LocalDate.now()
            startTime.value = LocalTime.now().minusHours(1)
            endTime.value = LocalTime.now()
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
        pickerProvider.pickTime("Select start time", startTime.value!!) {
            val minutes = getMinutes(startTime.value!!, endTime.value!!)
            startTime.value = it
            setEndTimeFromDuration(LocalTime.of(0, 0).plusMinutes(minutes))
        }
    }

    suspend fun pickDuration() {
        if (startTime.value != null && endTime.value != null) {
            val minutes = getMinutes(startTime.value!!, endTime.value!!)
            val duration = LocalTime.of(0, 0).plusMinutes(minutes)
            pickerProvider.pickDuration("Duration", duration) {
                setEndTimeFromDuration(it)
            }
        }
    }

    private fun setEndTimeFromDuration(it: LocalTime) {
        endTime.value = LocalTime.from(startTime.value).plusHours(it.hour.toLong())
            .plusMinutes(it.minute.toLong())
    }

    private fun addNote() {
        repository.addActivity(Activity(title.value!!,
            text.value!!,
            language.value!!,
            type.value!!,
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
            confidence.value!!,
            motivation.value!!,
            date.value!!,
            startTime.value!!,
            endTime.value!!)
    }

    fun save() {
        if (preparedId == null) {
            addNote()
        } else preparedId?.let { updateNote(it) }
    }
}

class EditActivityViewModelFactory(val id: String?, private val pickerProvider: PickerProvider) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditActivityViewModel(Repository.getRepository(), pickerProvider, id) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}