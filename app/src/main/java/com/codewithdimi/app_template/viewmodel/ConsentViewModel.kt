package com.codewithdimi.ankinudge.viewmodel

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codewithdimi.ankinudge.BuildConfig
import com.codewithdimi.ankinudge.data.CONSENT_TIMESTAMP
import com.codewithdimi.ankinudge.data.ConsentManager
import com.codewithdimi.ankinudge.data.SharedPreferenceProvider
import com.codewithdimi.ankinudge.data.SharedPreferenceStringLiveData

class ConsentViewModel(): ViewModel() {
    val manager = ConsentManager()
    val sharedPreferences = SharedPreferenceProvider.getProvider().sharedPreferences
    val consentTimestamp = SharedPreferenceStringLiveData(sharedPreferences, CONSENT_TIMESTAMP, "")
    val hasConsent = Transformations.map(consentTimestamp) { it.isNotEmpty() }

    init {
        // TODO: Remove
        if(BuildConfig.DEBUG) {
            with(sharedPreferences.edit()) {
                remove(com.codewithdimi.ankinudge.data.CONSENT_TIMESTAMP)
                apply()
            }
        }
    }

    fun consent() {
        manager.consent()
    }
}
