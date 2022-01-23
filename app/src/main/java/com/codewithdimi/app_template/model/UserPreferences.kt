package com.codewithdimi.app_template.model

import com.codewithdimi.app_template.utils.parseRealmTimeString
import com.codewithdimi.app_template.utils.toRealmTimeString
import com.codewithdimi.app_template.viewmodel.scheduleNotification
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where
import java.time.LocalTime

enum class PaidVersionStatus {
    Unknown,
    Free,
    Paid,
    Pending
}

open class UserPreferences : RealmObject() {

    @PrimaryKey
    var id = "UserPreferences_StaticID"
    var languages = RealmList<String>()
    var theme = ThemePreference.SYSTEM
    var reminderActive: Boolean = true
    private var _paidVersionStatus: String = PaidVersionStatus.Unknown.name
    var paidVersionStatus : PaidVersionStatus
        get() {
            return PaidVersionStatus.valueOf(_paidVersionStatus)
        }
        set(value) { _paidVersionStatus = value.name
        }

    private var _reminder: String? = toRealmTimeString(LocalTime.of(20,0))

    var reminder : LocalTime?
        get() {
            return _reminder?.let { parseRealmTimeString(it) }
        }
        set(value) { _reminder = value?.let { toRealmTimeString(it) }
        }

    companion object {
        fun createOrQuery(realm: Realm): LiveRealmObject<UserPreferences> {
            val queryUserPreferences = realm.where<UserPreferences>()
            val userPreferencesRes = queryUserPreferences.findFirst()

            if (userPreferencesRes == null) {
                realm.executeTransaction { tr -> tr.insert(UserPreferences()) }
                Realm.getApplicationContext()?.let { scheduleNotification(it, 20, 0) }
            }

            return LiveRealmObject(queryUserPreferences.findFirst())
        }
    }
}

object ThemePreference {
    const val LIGHT = "Light"
    const val DARK = "Dark"
    const val SYSTEM = "System default"
}