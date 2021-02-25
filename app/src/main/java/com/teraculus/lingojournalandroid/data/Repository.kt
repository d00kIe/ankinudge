package com.teraculus.lingojournalandroid.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teraculus.lingojournalandroid.model.LiveRealmResults
import com.teraculus.lingojournalandroid.model.Activity
import com.teraculus.lingojournalandroid.model.notesData
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmModel
import io.realm.Sort
import io.realm.kotlin.where
import org.bson.types.ObjectId
import java.util.*

fun <T : RealmModel?> MutableLiveData<List<T>?>.trigger() {
    value = value
}

class Repository {
    private var realm: Realm? = null
    private val activities: LiveData<List<Activity>?>

    init {
        initializeRealm()

        val queryAll = realm!!.where<Activity>().sort("date", Sort.DESCENDING)
        activities = LiveRealmResults<Activity>(queryAll.findAll())

        if(activities.value?.isEmpty()!!) {
            realm!!.executeTransaction { tr -> tr.insert(notesData()) }
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

    fun updateActivity(id: String, title: String, text: String, date: Date ) {
        val activity = getActivity(id)
        activity?.let {
            realm!!.executeTransaction {
                activity.title = title
                activity.text = text
                activity.date = date
            }
        }

        (activities as MutableLiveData<List<Activity>?>).trigger()
    }

    companion object {
        private  var INSTANCE: Repository? = null

        fun getRepository(): Repository {
            return synchronized(Repository::class) {
                val instance = INSTANCE ?: Repository()
                INSTANCE = instance
                instance
            }
        }
    }
}