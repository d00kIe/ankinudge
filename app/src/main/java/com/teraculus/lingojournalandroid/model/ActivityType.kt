package com.teraculus.lingojournalandroid.model

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where
import org.bson.types.ObjectId
import java.util.*

enum class UnitSelector() {
    TimePicker(),
    Count()
}

enum class MeasurementUnit(val id: String, val title: String, val unitSuffix: String, val selector: UnitSelector = UnitSelector.Count) {
    Time("time", "Duration", "", UnitSelector.TimePicker),
    Chapters("chapters", "Chapters", "chapters"),
    Pages("pages", "Pages", "pages"),
    Words("words", "Words", "words"),
    Articles("articles", "Articles", "articles"),
    Sessions("sessions", "Sessions", "sessions"),
    Classes("classes", "Classes", "classes"),
    Videos("videos", "Videos", "videos"),
    Items("items", "Items", "items")
}

open class ActivityType() : RealmObject() {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    private var categoryEnum: String = ""
    var name: String = ""
    var created: Date = Date()
    var lastUsed: Date? = null
    var unitEnum: String = "time"

    var category: ActivityCategory?
        get() = ActivityCategory.values().firstOrNull { it.title == categoryEnum }
        set(value) {
            this.categoryEnum = value?.title ?: ""
        }

    var unit: MeasurementUnit?
        get() = MeasurementUnit.values().firstOrNull { it.id == unitEnum }
        set(value) {
            this.unitEnum = value?.id ?: ""
        }

    constructor(category: ActivityCategory?, name: String, created: Date = Date(), lastUsed: Date? = null, unit: MeasurementUnit = MeasurementUnit.Time, id: ObjectId = ObjectId()) : this() {
        this.category = category
        this.name = name
        this.id = id
        this.created = created
        this.lastUsed = lastUsed
        this.unit = unit
    }

    companion object {
        fun createOrQuery(realm: Realm) : LiveRealmResults<ActivityType> {
            val queryTypes = realm.where<ActivityType>()
            val allTypes = queryTypes.findAll()
            val types = LiveRealmResults<ActivityType>(allTypes)

            if (allTypes.isEmpty()) {
                val initialData = activityTypeData()
                realm.executeTransaction { tr -> tr.insert(initialData) }
            }

            return types
        }
    }
}