package com.teraculus.lingojournalandroid.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teraculus.lingojournalandroid.model.*
import com.teraculus.lingojournalandroid.utils.asDate
import io.realm.*
import io.realm.kotlin.where
import org.bson.types.ObjectId
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

fun <T : RealmModel?> MutableLiveData<List<T>?>.trigger() {
    value = value
}

class Repository {
    private var realm: Realm? = null
    private val activities: LiveRealmResults<Activity>
    private val types: LiveData<List<ActivityType>?>
    private val userPreferences: LiveData<UserPreferences>
    private val goals: LiveRealmResults<ActivityGoal>

    init {
        initializeRealm()

        //realm!!.executeTransaction { realm!!.deleteAll() }

        // user preferences
        userPreferences = UserPreferences.createOrQuery(realm!!)

        // types
        types = ActivityType.createOrQuery(realm!!)

        // dummy activities
        activities = Activity.createOrQuery(realm!!, types.value)

        // goals
        goals = ActivityGoal.query(realm!!)
    }

    private fun initializeRealm() {
        val config = RealmConfiguration.Builder()
            //.deleteRealmIfMigrationNeeded() //TODO: Remove
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()
    }

    fun addActivity(activity: Activity) {
        realm!!.executeTransaction { tr -> tr.insert(activity) }
        updateLastLanguagePreference(activity.language)
    }

    fun addActivityType(type: ActivityType) {
        realm!!.executeTransaction { tr -> tr.insert(type) }
    }

    fun removeActivity(activity: Activity) {
        realm!!.executeTransaction {
            activity.deleteFromRealm()
        }
    }

    fun getActivity(id: String): LiveRealmObject<Activity?> {
        return LiveRealmObject(realm!!.where<Activity>().equalTo("id", ObjectId(id)).findFirst())
    }

    fun getActivities(): LiveRealmResults<Activity> {
        return activities
    }

    fun getAllActivities(): RealmResults<Activity>? {
        return realm!!.where<Activity>().findAll()
    }

    fun getActivities(date: LocalDate): RealmResults<Activity>? {
        return realm!!.where<Activity>().equalTo("_date", asDate(date)).findAllAsync()
    }

    fun getActivitiesFromBeginningTo(date: LocalDate): RealmResults<Activity>? {
        return realm!!.where<Activity>().lessThanOrEqualTo("_date", asDate(date)).findAllAsync()
    }

    fun getActivities(from: LocalDate, to: LocalDate): RealmResults<Activity>? {
        return realm!!.where<Activity>().between("_date", asDate(from), asDate(to)).findAllAsync()
    }

    fun updateActivity(
        id: String,
        title: String,
        text: String,
        language: String,
        type: ActivityType?,
        confidence: Float,
        motivation: Float,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime
    ) {
        val activity = getActivity(id).value
        activity?.let {
            realm!!.executeTransaction {
                activity.title = title
                activity.text = text
                activity.language = language
                activity.type = type
                activity.confidence = confidence
                activity.motivation = motivation
                activity.date = date
                activity.startTime = startTime
                activity.endTime = endTime
                activity.lastChangeTs = Instant.now().toEpochMilli()
            }
            updateLastLanguagePreference(language)
        }
    }

    fun getTypes(): LiveData<List<ActivityType>?> {
        return types
    }

    fun getUserPreferences(): LiveData<UserPreferences> {
        return userPreferences
    }

    private val maxRecentLangSize = 3
    private fun updateLastLanguagePreference(language: String) {
        realm!!.executeTransaction {
            userPreferences.value?.languages?.let { languages ->
            if(languages.find { it == language } == null) {
                languages.add(0,language)
            } else {
                languages.remove(language)
                languages.add(0,language)
            }

            if(languages.size > maxRecentLangSize)
                languages.removeAll(languages.takeLast(languages.size - maxRecentLangSize))
            }
        }
    }

    fun updateThemePreference(theme: String) {
        realm!!.executeTransaction {
            userPreferences.value?.theme = theme
        }
    }

    fun getActivityGoals(): LiveRealmResults<ActivityGoal> {
        return goals
    }

    fun getActivityGoal(id: String): LiveRealmObject<ActivityGoal?> {
        return LiveRealmObject(realm!!.where<ActivityGoal>().equalTo("id", ObjectId(id)).findFirst())
    }

    fun addActivityGoal(goal: ActivityGoal) {
        realm!!.executeTransaction { tr -> tr.insert(goal) }
    }

    fun removeActivityGoal(goal: ActivityGoal) {
        realm!!.executeTransaction {
            goal.deleteFromRealm()
        }
    }

    fun updateActivityGoal(
        id: String,
        title: String,
        text: String,
        language: String,
        activityType: ActivityType,
        date: LocalDate,
        weekDays: Array<Int>
    ) {
        val goal = getActivityGoal(id).value
        goal?.let {
            realm!!.executeTransaction {
                goal.text = text
                goal.language = language
                goal.activityType = activityType
                goal.date = date
                goal.lastChangeTs = Instant.now().toEpochMilli()
                goal.weekDays = RealmList(*weekDays)
            }
        }
    }

    companion object {
        private var INSTANCE: Repository? = null

        fun getRepository(): Repository {
            return synchronized(Repository::class) {
                val instance = INSTANCE ?: Repository()
                INSTANCE = instance
                instance
            }
        }
    }
}