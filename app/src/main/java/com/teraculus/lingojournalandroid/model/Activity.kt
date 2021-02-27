package com.teraculus.lingojournalandroid.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId
import java.util.*

open class Activity() :
    RealmObject() {

    @PrimaryKey
    var id = ObjectId()

//    var language: String = "" TODO
    @Required
    var title: String = ""
    var text: String = ""
    var type: ActivityType? = null

    @Required
    var startDate: Date = Date()

    @Required
    var endDate: Date = Date()
    var confidence: Int = 100
    var motivation: Int = 100

    constructor(
        title: String,
        text: String,
        type: ActivityType?,
        confidence: Int = 100,
        motivation: Int = 100,
        startDate: Date = Date(System.currentTimeMillis() - 3600 * 1000),
        endDate: Date = Date(),
        id: ObjectId = ObjectId(),
    ) : this() {
        this.id = id
        this.title = title
        this.text = text
        this.type = type
        this.startDate = startDate
        this.endDate = endDate
        this.confidence = confidence
        this.motivation = motivation
    }
}