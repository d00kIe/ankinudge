package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.*
import com.teraculus.lingojournalandroid.PickerProvider
import com.teraculus.lingojournalandroid.data.Language
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.data.getAllLanguages
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.ActivityType
import java.time.LocalDate
import java.time.LocalTime

class EditActivityViewModel(private val repository: Repository, private val pickerProvider: PickerProvider) : ViewModel() {
    val types = repository.getTypes()
    val languages = MutableLiveData(getAllLanguages())
    val date = MutableLiveData(LocalDate.now())
    val startTime = MutableLiveData(LocalTime.now().minusHours(1))
    val endTime = MutableLiveData(LocalTime.now())
    val title = MutableLiveData("")
    val text = MutableLiveData("")
    val language = MutableLiveData("")
    val type  = MutableLiveData(types.value!!.first())
    val confidence = MutableLiveData(100)
    val motivation = MutableLiveData(100)

    fun prepareActivity(id: String?) {
        val activity = id?.let { repository.getActivity(it) }
        if (activity != null) {
            title.value = activity.title
            text.value = activity.text
            language.value = activity.language
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
            confidence.value = 50
            motivation.value = 50
            date.value = LocalDate.now()
            startTime.value = LocalTime.now().minusHours(1)
            endTime.value = LocalTime.now()
        }
    }

    fun onTitleChange(value: String) {
        title.value = value
    }

    fun onTextChange(value: String) {
        text.value = value
    }

    fun onTypeChange(value: ActivityType) {
        type.value = value
    }

    fun onLanguageChange(value: String) {
        language.value = value // language code
    }

    fun onConfidenceChange(value: Float) {
        confidence.value = value.toInt()
    }

    fun onMotivationChange(value: Float) {
        motivation.value = value.toInt()
    }

    fun pickDate() {
        pickerProvider.pickDate(null, date.value!!) { date.value = it; }
    }

    fun pickStartTime() {
        pickerProvider.pickTime("Select start time", startTime.value!!) { startTime.value = it }
    }

    fun pickEndTime() {
        pickerProvider.pickTime("Select end time", endTime.value!!) { endTime.value = it }
    }

    fun addNote() {
        repository.addActivity(Activity(title.value!!, text.value!!, language.value!!, null, confidence.value!!, motivation.value!!, date.value!!, startTime.value!!, endTime.value!!))
    }

    fun updateNote(id: String) {
        repository.updateActivity(id, title.value!!, text.value!!,null, confidence.value!!, motivation.value!!, date.value!!, startTime.value!!, endTime.value!!)
    }
}

class EditActivityViewModelFactory(private val pickerProvider: PickerProvider) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditActivityViewModel(Repository.getRepository(), pickerProvider) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}