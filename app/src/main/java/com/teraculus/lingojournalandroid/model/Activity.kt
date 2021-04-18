package com.teraculus.lingojournalandroid.model

import com.teraculus.lingojournalandroid.utils.*
import io.realm.Realm
import io.realm.RealmObject
import io.realm.Sort
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import io.realm.kotlin.where
import org.bson.types.ObjectId
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

open class Activity() :
    RealmObject() {

    @PrimaryKey
    var id = ObjectId()

    @Required
    var title: String = ""
    var language: String = ""
    var text: String = ""
    var type: ActivityType? = null
    var confidence: Float = 100f
    var motivation: Float = 100f
    var lastChangeTs: Long = 0 // allows LiveData to update correctly if only "type" was changed, see LiveRealmObject::onActive

    @Required private var _date: Date = asDate(LocalDate.now())
    @Required private var _startTime: String = toRealmTimeString(LocalTime.now().minusHours(1))
    private var _endTime: String = toRealmTimeString(LocalTime.now())

    var date : LocalDate
        get() {
            return asLocalDate(_date)
        }
        set(value) { _date = asDate(value) }

    var startTime : LocalTime
        get() {
            return parseRealmTimeString(_startTime)
        }
        set(value) { _startTime = toRealmTimeString(value) }

    var endTime : LocalTime
        get() {
            return parseRealmTimeString(_endTime)
        }
        set(value) { _endTime = toRealmTimeString(value) }

    constructor(
        title: String,
        text: String,
        language: String,
        type: ActivityType?,
        confidence: Float = 100f,
        motivation: Float = 100f,
        date: LocalDate = LocalDate.now(),
        startTime: LocalTime = LocalTime.now().minusHours(1),
        endTime: LocalTime = LocalTime.now(),
        id: ObjectId = ObjectId(),
    ) : this() {
        this.id = id
        this.title = title
        this.text = text
        this.language = language
        this.type = type
        this.date = date
        this.startTime = startTime
        this.endTime = endTime
        this.confidence = confidence
        this.motivation = motivation
        this.lastChangeTs = Instant.now().toEpochMilli()
    }

    companion object {
        val DEFAULT = Activity("","", "", null)

        fun createOrQuery(realm: Realm, value: List<ActivityType>?) : LiveRealmResults<Activity> {
            val queryActivities = realm.where<Activity>().sort("_date", Sort.DESCENDING) //TODO
            val res = queryActivities.findAll()
            val activities = LiveRealmResults<Activity>(res)

            if (res.isEmpty()) {
                realm.executeTransaction { tr -> tr.insert(activityData(value!!)) }
            }

            return activities
        }
    }

}