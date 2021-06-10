package com.teraculus.lingojournalandroid.data

import android.util.Range
import androidx.lifecycle.LiveData
import com.teraculus.lingojournalandroid.model.*
import com.teraculus.lingojournalandroid.utils.*
import io.realm.*
import io.realm.kotlin.where
import org.bson.types.ObjectId
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

private fun getMinutesFromDynamicRealmObject(obj: DynamicRealmObject): Long {
    val startTime = parseRealmTimeString(obj.getString("_startTime"))
    val endTime = parseRealmTimeString(obj.getString("_endTime"))
    return getMinutes(startTime, endTime)
}

class PreferencesRepo(val repo: Repository) {
    private val maxRecentLangSize = 3
    private val userPreferences: LiveData<UserPreferences>

    init {
        // user preferences
        userPreferences = UserPreferences.createOrQuery(repo.realm!!)
    }

    fun all(): LiveData<UserPreferences> {
        return userPreferences
    }

    fun updateLastLanguage(language: String) {
        repo.realm!!.executeTransaction {
            userPreferences.value?.languages?.let { languages ->
                if (languages.find { it == language } == null) {
                    languages.add(0, language)
                } else {
                    languages.remove(language)
                    languages.add(0, language)
                }

                if (languages.size > maxRecentLangSize)
                    languages.removeAll(languages.takeLast(languages.size - maxRecentLangSize))
            }
        }
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
}

class ActivityTypeRepo(val repo: Repository) {
    private val types: LiveData<List<ActivityType>?>

    init {
        // types
        types = ActivityType.createOrQuery(repo.realm!!)
    }

    fun add(type: ActivityType) {
        repo.realm!!.executeTransaction { tr -> tr.insert(type) }
    }

    fun all(): LiveData<List<ActivityType>?> {
        return types
    }

    fun remove(it: ActivityType) {
        repo.realm!!.executeTransaction {tr ->
            tr.where<Activity>().equalTo("type.id", it.id).findAll().deleteAllFromRealm()
            tr.where<ActivityGoal>().equalTo("activityType.id", it.id).findAll().deleteAllFromRealm()
            it.deleteFromRealm()
        }
    }
}

class ActivityRepo(val repo: Repository) {
    private val activities: LiveRealmResults<Activity>

    init {
        // dummy activities
        activities = Activity.createOrQuery(repo.realm!!, repo.types.all().value)
    }

    fun add(activity: Activity) {
        repo.realm!!.executeTransaction { tr -> tr.insert(activity) }
        repo.preferences.updateLastLanguage(activity.language)
    }

    fun remove(activity: Activity) {
        repo.realm!!.executeTransaction {
            activity.deleteFromRealm()
        }
    }

    fun get(id: String): LiveRealmObject<Activity?> {
        return LiveRealmObject(repo.realm!!.where<Activity>().equalTo("id", ObjectId(id)).findFirst())
    }

    private fun tryAddLanguageQuery(query: RealmQuery<Activity>, language: String? = null): RealmQuery<Activity> {
        return if(language?.isNotEmpty() == true) {
            query.equalTo("language", language)
        } else {
            query
        }
    }

    fun allLive(): LiveRealmResults<Activity> {
        return activities
    }

    fun all(language: String? = null): RealmResults<Activity>? {
        return tryAddLanguageQuery(repo.realm!!.where(), language).findAll()
    }

    fun all(date: LocalDate, language: String? = null): RealmResults<Activity>? {
        return tryAddLanguageQuery(repo.realm!!.where<Activity>().equalTo("_date", asDate(date)), language).findAllAsync()
    }

    fun allLive(date: LocalDate, language: String? = null): LiveRealmResults<Activity> {
        return LiveRealmResults(all(date, language))
    }

    fun all(from: LocalDate, to: LocalDate, language: String? = null): RealmResults<Activity>? {
        return repo.realm!!.where<Activity>().between("_date", asDate(from), asDate(to)).findAllAsync()
    }

    fun allLive(from: LocalDate, to: LocalDate, language: String? = null): LiveRealmResults<Activity> {
        return LiveRealmResults(all(from, to, language))
    }

    fun allUntil(date: LocalDate, language: String? = null): RealmResults<Activity>? {
        return tryAddLanguageQuery(repo.realm!!.where<Activity>().lessThanOrEqualTo("_date", asDate(date)).sort("_date", Sort.DESCENDING), language).findAllAsync()
    }

    fun allUntilLive(date: LocalDate, language: String? = null): LiveRealmResults<Activity> {
        return LiveRealmResults(allUntil(date, language))
    }

    fun update(
        id: String,
        title: String,
        text: String,
        language: String,
        type: ActivityType?,
        unitCount: Float,
        confidence: Float,
        motivation: Float,
        date: LocalDate,
        startTime: LocalTime,
        duration: Int,
    ) {
        val activity = get(id).value
        activity?.let {
            repo.realm!!.executeTransaction {
                activity.title = title
                activity.text = text
                activity.language = language
                activity.type = type
                activity.confidence = confidence
                activity.motivation = motivation
                activity.unitCount = unitCount
                activity.date = date
                activity.startTime = startTime
                activity.duration = duration
                activity.lastChangeTs = Instant.now().toEpochMilli()
            }
            repo.preferences.updateLastLanguage(language)
        }
    }
}

class ActivityGoalRepo(val repo: Repository) {
    private val goals: LiveRealmResults<ActivityGoal> = ActivityGoal.query(repo.realm!!)

    private fun tryAddLanguageQuery(query: RealmQuery<ActivityGoal>, language: String? = null): RealmQuery<ActivityGoal> {
        return if(language?.isNotEmpty() == true) {
            query.equalTo("language", language)
        } else {
            query
        }
    }

    fun all(): LiveRealmResults<ActivityGoal> {
        return goals
    }

    fun all(type: GoalType? = null, active: Boolean? = null, range: Range<LocalDate>? = null, language: String? = null): LiveRealmResults<ActivityGoal> {
        val query = repo.realm!!.where<ActivityGoal>().apply {
            type?.let { equalTo("goalType", type.id) }
            active?.let { equalTo("active", active) }
            language?.let { equalTo("language", language) }
            range?.let {
                val date = asDate(range.lower)
                val endDate = asDate(range.upper)
                beginGroup()
                between("_date", date, endDate)
                or()
                between("_endDate",date, endDate)
                endGroup()
            }
        }

        return LiveRealmResults(query.findAll())
    }

    fun get(id: String): LiveRealmObject<ActivityGoal?> {
        return LiveRealmObject(repo.realm!!.where<ActivityGoal>().equalTo("id", ObjectId(id))
            .findFirst())
    }

    fun insertOrUpdate(goal: ActivityGoal) {
        goal.lastChangeTs = Instant.now().toEpochMilli()
        repo.realm!!.executeTransaction { tr -> tr.insertOrUpdate(goal) }
    }

    fun remove(goalId: ObjectId) {
        val goal = get(goalId.toString()).value
        goal?.let {
            repo.realm!!.executeTransaction {
                goal.deleteFromRealm()
            }
        }
    }
}

class Repository {
    var realm: Realm? = null
    val activities: ActivityRepo
    val types: ActivityTypeRepo
    val preferences: PreferencesRepo
    val goals: ActivityGoalRepo

    init {
        initializeRealm()

        //realm!!.executeTransaction { realm!!.deleteAll() }

        // user preferences
        preferences = PreferencesRepo(this)

        // types
        types = ActivityTypeRepo(this)

        // activities - should be initialized after "types"!
        activities = ActivityRepo(this)

        // goals
        goals = ActivityGoalRepo(this)
    }

    private fun initializeRealm() {
        val migration = RealmMigration { realm, oldVersion, _ ->
            var version: Long = oldVersion
            // DynamicRealm exposes an editable schema
            val schema: RealmSchema = realm.schema

            // Changes from version 0 to 1: Adding ActivityGoal.
            if (version == 0L) {
                schema.create("ActivityGoal")
                    .addField("id",
                        ObjectId::class.java,
                        FieldAttribute.PRIMARY_KEY,
                        FieldAttribute.REQUIRED)
                    .addField("language", String::class.java, FieldAttribute.REQUIRED)
                    .addField("text", String::class.java, FieldAttribute.REQUIRED)
                    .addRealmObjectField("activityType", schema.get("ActivityType")!!)
                    .addField("goalType", String::class.java, FieldAttribute.REQUIRED)
                    .addField("lastChangeTs", Long::class.java)
                    .addRealmListField("weekDays", Integer::class.java)
                    .addField("active", Boolean::class.java)
                    .addField("_date", Date::class.java, FieldAttribute.REQUIRED)
                    .addField("_reminder", Date::class.java)

                version++
            }

            if (version == 1L) {
                schema.get("UserPreferences")!!
                    .addField("_reminder", String::class.java)
                    .addField("reminderActive", Boolean::class.java)
                    .transform { obj: DynamicRealmObject ->
                        obj.setString("_reminder", toRealmTimeString(LocalTime.of(20,0)))
                        obj.setBoolean("reminderActive", false)
                    }

                version++
            }

            if (version == 2L) {
                schema.get("ActivityType")!!
                    .addField("unitEnum", String::class.java, FieldAttribute.REQUIRED)
                    .addPrimaryKey("id")
                    .transform { obj: DynamicRealmObject ->
                        obj.setString("unitEnum", "time")
                    }

                schema.get("Activity")!!
                    .addField("unitCount", Float::class.java)
                    .addField("duration", Int::class.java)
                    .transform { obj: DynamicRealmObject ->
                        obj.setFloat("unitCount", 1f)
                        obj.setInt("duration", getMinutesFromDynamicRealmObject(obj).toInt())
                    }
                    .removeField("_endTime")

                schema.get("ActivityGoal")!!
                    .addField("_endDate", Date::class.java)
                    .addField("_effortUnit", String::class.java)
                    .addField("durationGoal", Int::class.java)
                    .setNullable("durationGoal", true)
                    .addField("unitCountGoal", Float::class.java)
                    .setNullable("unitCountGoal", true)
                    .addField("lastActiveChange", Date::class.java, FieldAttribute.REQUIRED)
                    .transform { obj: DynamicRealmObject ->
                        obj.setDate("lastActiveChange", asDate(LocalDate.now()))
                        obj.setString("_effortUnit", "time")
                        obj.setLong("durationGoal", 60)
                        obj.setFloat("unitCountGoal", 1f)
                        // set _endDate to something meaningful, null will crash the app when converting old daily goal to long-term
                        val date = asLocalDate(obj.getDate("_date"))
                        obj.setDate("_endDate", asDate(date.plusMonths(1)))
                    }
                //version++
            }
        }

        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .schemaVersion(3)
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