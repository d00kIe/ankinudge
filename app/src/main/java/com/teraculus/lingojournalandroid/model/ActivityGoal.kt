package com.teraculus.lingojournalandroid.model

import com.teraculus.lingojournalandroid.utils.asDate
import com.teraculus.lingojournalandroid.utils.asLocalDate
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.kotlin.where
import org.bson.types.ObjectId
import java.time.Instant
import java.time.LocalDate
import java.util.*

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

    @Required
    private var _date: Date = asDate(LocalDate.now())

    var date : LocalDate
        get() {
            return asLocalDate(_date)
        }
        set(value) { _date = asDate(value) }

    private var _reminder: Date? = asDate(LocalDate.now())

    var reminder : LocalDate?
        get() {
            return _reminder?.let { asLocalDate(it) }
        }
        set(value) { _reminder = value?.let { asDate(it) }
        }

    constructor(
        text: String,
        language: String,
        activityType: ActivityType,
        date: LocalDate = LocalDate.now(),
        id: ObjectId = ObjectId(),
        reminder: LocalDate? = null,
        weekDays: Array<Int>
    ) : this() {
        this.id = id
        this.text = text
        this.language = language
        this.activityType = activityType
        this.date = date
        this.lastChangeTs = Instant.now().toEpochMilli()
        this.reminder = reminder
        this.weekDays = RealmList(*weekDays)
    }


    companion object {
        fun query(realm: Realm) : LiveRealmResults<ActivityGoal> {
            val queryActivities = realm.where<ActivityGoal>().sort("_date", Sort.DESCENDING) //TODO
            val res = queryActivities.findAll()
            return LiveRealmResults<ActivityGoal>(res)
        }
    }
}