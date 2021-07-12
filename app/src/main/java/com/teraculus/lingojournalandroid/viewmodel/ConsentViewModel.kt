package com.teraculus.lingojournalandroid.viewmodel

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.teraculus.lingojournalandroid.BuildConfig
import com.teraculus.lingojournalandroid.data.CONSENT_TIMESTAMP
import com.teraculus.lingojournalandroid.data.ConsentManager
import com.teraculus.lingojournalandroid.data.SharedPreferenceProvider
import com.teraculus.lingojournalandroid.data.SharedPreferenceStringLiveData

class ConsentViewModel(): ViewModel() {
    val manager = ConsentManager()
    val sharedPreferences = SharedPreferenceProvider.getProvider().sharedPreferences
    val consentTimestamp = SharedPreferenceStringLiveData(sharedPreferences, CONSENT_TIMESTAMP, "")
    val hasConsent = Transformations.map(consentTimestamp) { it.isNotEmpty() }

    init {
        // TODO: Remove
        if(BuildConfig.DEBUG) {
            with(sharedPreferences.edit()) {
                remove(CONSENT_TIMESTAMP)
                apply()
            }
        }
    }

    fun consent() {
        manager.consent()
    }
}
