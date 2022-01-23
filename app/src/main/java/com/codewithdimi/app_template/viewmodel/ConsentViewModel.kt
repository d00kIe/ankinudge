package com.codewithdimi.app_template.viewmodel

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codewithdimi.app_template.BuildConfig
import com.codewithdimi.app_template.data.CONSENT_TIMESTAMP
import com.codewithdimi.app_template.data.ConsentManager
import com.codewithdimi.app_template.data.SharedPreferenceProvider
import com.codewithdimi.app_template.data.SharedPreferenceStringLiveData

class ConsentViewModel(): ViewModel() {
    val manager = ConsentManager()
    val sharedPreferences = SharedPreferenceProvider.getProvider().sharedPreferences
    val consentTimestamp = SharedPreferenceStringLiveData(sharedPreferences, CONSENT_TIMESTAMP, "")
    val hasConsent = Transformations.map(consentTimestamp) { it.isNotEmpty() }

    init {
        // TODO: Remove
        if(BuildConfig.DEBUG) {
            with(sharedPreferences.edit()) {
                remove(com.codewithdimi.app_template.data.CONSENT_TIMESTAMP)
                apply()
            }
        }
    }

    fun consent() {
        manager.consent()
    }
}
