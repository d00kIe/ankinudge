package com.teraculus.lingojournalandroid.model

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where
import org.bson.types.ObjectId

open class UserPreferences : RealmObject() {

    @PrimaryKey
    var id = "UserPreferences_StaticID"
    var languages = RealmList<String>()
    var theme = ThemePreference.SYSTEM

    companion object {
        fun createOrQuery(realm: Realm) : LiveRealmObject<UserPreferences> {
            val queryUserPreferences = realm.where<UserPreferences>()
            val userPreferences = LiveRealmObject<UserPreferences>(queryUserPreferences.findFirst())

            if(userPreferences.value == null) {
                realm.executeTransaction { tr -> tr.insert(UserPreferences()) }
            }

            return  userPreferences
        }
    }
}

object ThemePreference {
    const val LIGHT = "Light"
    const val DARK = "Dark"
    const val SYSTEM = "System"
}