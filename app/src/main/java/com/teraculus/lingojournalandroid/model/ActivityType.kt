package com.teraculus.lingojournalandroid.model

import io.realm.RealmObject
import org.bson.types.ObjectId
import java.util.*

open class ActivityType() : RealmObject() {
    private var categoryEnum: String = ""
    var name: String = ""
    var id: ObjectId = ObjectId()
    var created: Date = Date()
    var lastUsed: Date? = null

    var category: ActivityCategory?
        get() = ActivityCategory.values().firstOrNull() { it.title == categoryEnum }
        set(value) {
            this.categoryEnum = value?.title ?: ""
        }

    constructor(category: ActivityCategory?, name: String, created: Date = Date(), lastUsed: Date? = null, id: ObjectId = ObjectId()) : this() {
        this.category = category
        this.name = name
        this.id = id
        this.created = created
        this.lastUsed = lastUsed
    }
}