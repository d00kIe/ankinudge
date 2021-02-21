package com.teraculus.lingojournalandroid.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId
import java.util.*

fun createDate(year: Int,
               month: Int,
               day: Int,
               hrs: Int,
               min: Int) : Date {
    var c = Calendar.getInstance()
    c.set(year, month, day, hrs, min)
    return c.time
}

open class Note() :

    RealmObject() {

    @PrimaryKey
    var id = ObjectId()

    var title: String = ""
    var text: String = ""
    var date: Date = Date()

    constructor(title: String, text: String, dateTime: Date, id: ObjectId = ObjectId()) : this() {
        this.id = id
        this.title = title
        this.text = text
        this.date = dateTime
    }
}