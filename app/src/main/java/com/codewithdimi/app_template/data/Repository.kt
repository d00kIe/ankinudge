package com.codewithdimi.ankinudge.data

import androidx.lifecycle.LiveData
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.codewithdimi.ankinudge.model.PaidVersionStatus
import com.codewithdimi.ankinudge.model.UserPreferences
import com.codewithdimi.ankinudge.utils.getMinutes
import com.codewithdimi.ankinudge.utils.parseRealmTimeString
import io.realm.*
import java.time.LocalTime

class PreferencesRepo(val repo: Repository) {
    private val maxRecentLangSize = 4
    private val userPreferences: LiveData<UserPreferences>

    init {
        // user preferences
        userPreferences = UserPreferences.createOrQuery(repo.realm!!)

    }

    fun all(): LiveData<UserPreferences> {
        return userPreferences
    }


    fun updateTheme(theme: String) {
        repo.realm!!.executeTransaction {
            userPreferences.value?.theme = theme
        }
    }

    fun updateReminderActive(active: Boolean) {
        repo.realm!!.executeTransaction {
            userPreferences.value?.reminderActive = active
        }
    }

    fun updateReminder(time: LocalTime) {
        repo.realm!!.executeTransaction {
            userPreferences.value?.reminder = time
        }
    }

    fun updatePaidVersionStatus(status: PaidVersionStatus) {
        repo.realm!!.executeTransaction {
            userPreferences.value?.paidVersionStatus = status
        }
    }
}

class Repository {
    var realm: Realm? = null
    val preferences: PreferencesRepo
//    val firebaseAnalytics: FirebaseAnalytics

    init {
        initializeRealm()

        //realm!!.executeTransaction { realm!!.deleteAll() }

        // user preferences
        preferences = PreferencesRepo(this)

        // analytics
//        firebaseAnalytics = Firebase.analytics
    }

    private fun initializeRealm() {
        val migration = RealmMigration { realm, oldVersion, _ ->
            var version: Long = oldVersion
            // DynamicRealm exposes an editable schema
            val schema: RealmSchema = realm.schema

            // Changes from version 0 to 1: Adding ActivityGoal.
//            if (version == 0L) {
//                schema.create("ActivityGoal")
//                    .addField("id",
//                        ObjectId::class.java,
//                        FieldAttribute.PRIMARY_KEY,
//                        FieldAttribute.REQUIRED)
//                    .addField("language", String::class.java, FieldAttribute.REQUIRED)
//                    .addField("text", String::class.java, FieldAttribute.REQUIRED)
//                    .addRealmObjectField("activityType", schema.get("ActivityType")!!)
//                    .addField("goalType", String::class.java, FieldAttribute.REQUIRED)
//                    .addField("lastChangeTs", Long::class.java)
//                    .addRealmListField("weekDays", Integer::class.java)
//                    .addField("active", Boolean::class.java)
//                    .addField("_date", Date::class.java, FieldAttribute.REQUIRED)
//                    .addField("_reminder", Date::class.java)
//
//                version++
//            }
        }

        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .schemaVersion(0)
            .migration(migration)
            .build()

        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()
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