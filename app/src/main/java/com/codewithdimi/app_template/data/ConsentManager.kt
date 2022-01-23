package com.codewithdimi.app_template.data

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

const val CONSENT_TIMESTAMP = "consent_timestamp"
class ConsentManager {
    val sharedPreferences = SharedPreferenceProvider.getProvider().sharedPreferences

    fun consent() {
        with(sharedPreferences.edit()) {
            putString(CONSENT_TIMESTAMP, java.time.LocalDateTime.now().toString())
            apply()
        }

        Firebase.analytics.setAnalyticsCollectionEnabled(true)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
    }

    fun hasConsent(): Boolean? {
        return SharedPreferenceProvider.getProvider().sharedPreferences.contains(CONSENT_TIMESTAMP)
    }
}