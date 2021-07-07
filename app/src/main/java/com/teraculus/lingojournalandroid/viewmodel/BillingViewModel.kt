package com.teraculus.lingojournalandroid.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.*
import com.android.billingclient.api.BillingClient
import com.teraculus.lingojournalandroid.data.BillingManager
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.PaidVersionStatus
import kotlinx.coroutines.launch

class BillingViewModel : ViewModel() {
    private val billingManager = BillingManager.getManager()
    private val preferences = Repository.getRepository().preferences.all()

    val canPurchase: LiveData<Boolean> = preferences.switchMap { pref ->
        liveData {
            val purchases = billingManager.queryPurchases()
            val result =
                if(purchases.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchases.purchasesList.isEmpty() // if we connect and don't have purchases
                }
                else {
                    Log.w("BillingViewModel", purchases.billingResult.responseCode.toString())
                    pref.paidVersionStatus != PaidVersionStatus.Paid // if we don't connect and status is not Paid
                }

            emit(result)
        }
    }

    init {
        this.viewModelScope.launch {
            billingManager.ensureConnected()
            if(billingManager.alreadyPurchased())
                Repository.getRepository().preferences.updatePaidVersionStatus(PaidVersionStatus.Paid)
        }
    }

    fun tryPurchase(context: Activity) {
        viewModelScope.launch {
            if(billingManager.ensureConnected()) {
                val result = billingManager.launchBillingFlow(context)
                result?.let { pr ->
                    if(pr.billingResult.responseCode == BillingClient.BillingResponseCode.OK && pr.purchasesList.isNotEmpty()) {
                        if(pr.purchasesList.size == 1) {
                            if(billingManager.acknowlidgePurchase(pr.purchasesList[0])) {
                                Repository.getRepository().preferences.updatePaidVersionStatus(PaidVersionStatus.Paid)
                            }
                        }
                    }
                }
            }
        }
    }
}