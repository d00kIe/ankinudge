package com.teraculus.lingojournalandroid.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teraculus.lingojournalandroid.model.*
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmModel
import io.realm.Sort
import io.realm.kotlin.where
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

fun <T : RealmModel?> MutableLiveData<List<T>?>.trigger() {
    value = value
}

class Repository {
    private var realm: Realm? = null
    private val activities: LiveData<List<Activity>?>
    private val types: LiveData<List<ActivityType>?>

    init {
        initializeRealm()

        // types
        val queryTypes = realm!!.where<ActivityType>()
        types = LiveRealmResults<ActivityType>(queryTypes.findAll())

        if (types.value?.isEmpty()!!) {
            realm!!.executeTransaction { tr -> tr.insert(activityTypeData()) }
        }

        // dummy activities
        val queryActivities = realm!!.where<Activity>().sort("_date", Sort.DESCENDING) //TODO
        activities = LiveRealmResults<Activity>(queryActivities.findAll())

        if (activities.value?.isEmpty()!!) {
            realm!!.executeTransaction { tr -> tr.insert(activityData()) }
        }
    }

    private fun initializeRealm() {
        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()
    }

    fun addActivity(activity: Activity) {
        realm!!.executeTransaction { tr -> tr.insert(activity) }
    }

    fun removeActivity(activity: Activity) {
        realm!!.executeTransaction {
            activity.deleteFromRealm()
        }
    }

    fun getActivity(id: String): Activity? {
        return realm!!.where<Activity>().equalTo("id", ObjectId(id)).findFirst()
    }

    fun getActivities(): LiveData<List<Activity>?> {
        return activities
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
        val activity = getActivity(id)
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
            }
        }

        //(activities as MutableLiveData<List<Activity>?>).trigger()
    }

    fun getTypes(): LiveData<List<ActivityType>?> {
        return types
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