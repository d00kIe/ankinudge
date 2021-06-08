package com.teraculus.lingojournalandroid.model

import com.teraculus.lingojournalandroid.utils.asDate
import com.teraculus.lingojournalandroid.utils.asLocalDate
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.kotlin.where
import org.bson.types.ObjectId
import java.time.Instant
import java.time.LocalDate
import java.util.*

enum class GoalType(val id: String, val title: String) {
    Daily("daily", "Daily"),
    LongTerm("longterm", "Long-term")
}

enum class EffortUnit(val id: String) {
    Time("time"),
    Unit("unit")
}

open class ActivityGoal() : RealmObject() {
    @PrimaryKey
    var id = ObjectId()

    @Required
    var language: String = ""
    var text: String = ""
    var activityType: ActivityType? = null
    var goalType: String = "daily"
    var lastChangeTs: Long = 0 // allows LiveData to update correctly if only "type" was changed, see LiveRealmObject::onActive
    var weekDays: RealmList<Int> = RealmList<Int>()
    var active: Boolean = true
    var lastActiveChange: Date = asDate(LocalDate.now())

    // effort
    var durationGoal: Int? = 60 // new
    var unitCountGoal: Float? = 1f // new
    private var _effortUnit: String = "time" // new

    @Required
    private var _date: Date = asDate(LocalDate.now())
    private var _endDate: Date? = asDate(LocalDate.now().plusMonths(1)) // new
    private var _reminder: Date? = asDate(LocalDate.now())

    var type: GoalType
        get() {
            return GoalType.values().first { g -> g.id == goalType }
        }
        set(value) {
            goalType = value.id
        }

    var effortUnit: EffortUnit
        get() {
            return EffortUnit.values().first { u -> u.id == _effortUnit }
        }
        set(value) {
            _effortUnit = value.id
        }

    var date: LocalDate
        get() {
            return asLocalDate(_date)
        }
        set(value) {
            _date = asDate(value)
        }

    var endDate: LocalDate?
        get() {
            return _endDate?.let { asLocalDate(it) }
        }
        set(value) {
            _endDate = value?.let { asDate(it) }
        }

    var reminder: LocalDate?
        get() {
            return _reminder?.let { asLocalDate(it) }
        }
        set(value) {
            _reminder = value?.let { asDate(it) }
        }

    constructor(
        text: String,
        language: String,
        activityType: ActivityType,
        date: LocalDate = LocalDate.now(),
        endDate: LocalDate? = LocalDate.now().plusMonths(1),
        id: ObjectId = ObjectId(),
        reminder: LocalDate? = null,
        weekDays: Array<Int>,
        effortUnit: EffortUnit = EffortUnit.Time,
        durationGoal: Int? = 60,
        unitCountGoal: Float? = 1f
    ) : this() {
        this.id = id
        this.text = text
        this.language = language
        this.activityType = activityType
        this.date = date
        this.endDate = endDate
        this.lastChangeTs = Instant.now().toEpochMilli()
        this.reminder = reminder
        this.weekDays = RealmList(*weekDays)
        this.effortUnit = effortUnit
        this.durationGoal = durationGoal
        this.unitCountGoal = unitCountGoal
    }

    companion object {
        fun query(realm: Realm): LiveRealmResults<ActivityGoal> {
            val queryActivities = realm.where<ActivityGoal>().sort("_date", Sort.DESCENDING) //TODO
            val res = queryActivities.findAll()
            return LiveRealmResults<ActivityGoal>(res)
        }
    }
}