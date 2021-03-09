package com.teraculus.lingojournalandroid.model

import com.teraculus.lingojournalandroid.utils.parseRealmDateString
import com.teraculus.lingojournalandroid.utils.parseRealmTimeString
import com.teraculus.lingojournalandroid.utils.toRealmDateString
import com.teraculus.lingojournalandroid.utils.toRealmTimeString
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

open class Activity() :
    RealmObject() {

    @PrimaryKey
    var id = ObjectId()

//    var language: String = "" TODO
    @Required
    var title: String = ""
    var language: String = ""
    var text: String = ""
    var type: ActivityType? = null
    var confidence: Float = 100f
    var motivation: Float = 100f

    @Required private var _date: String = toRealmDateString(LocalDate.now())
    @Required private var _startTime: String = toRealmTimeString(LocalTime.now().minusHours(1))
    private var _endTime: String = toRealmTimeString(LocalTime.now())

    var date : LocalDate
        get() {
            return parseRealmDateString(_date)
        }
        set(value) { _date = toRealmDateString(value) }

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
    }

    companion object {
        val DEFAULT = Activity("","", "", null)
    }
}