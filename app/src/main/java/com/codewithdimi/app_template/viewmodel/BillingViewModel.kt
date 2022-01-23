package com.codewithdimi.app_template.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.codewithdimi.app_template.data.BillingManager
import com.codewithdimi.app_template.data.Repository
import com.codewithdimi.app_template.model.PaidVersionStatus
import kotlinx.coroutines.launch

class BillingViewModel : ViewModel() {
//    private val billingManager = BillingManager.getManager()
    private val preferences = Repository.getRepository().preferences.all()

    val canPurchase: LiveData<Boolean> = Transformations.map(preferences) {
        pref -> pref.paidVersionStatus != PaidVersionStatus.Paid
    }

    init {
        this.viewModelScope.launch {
//            billingManager.ensureConnected()
//            if(billingManager.alreadyAcknowledged())
//                Repository.getRepository().preferences.updatePaidVersionStatus(PaidVersionStatus.Paid)
        }
    }

    fun tryPurchase(context: Activity) {
        viewModelScope.launch {
//            if(billingManager.ensureConnected()) {
//                val result = billingManager.launchBillingFlow(context)
//                result?.let { pr ->
//                    if(pr.billingResult.responseCode == BillingClient.BillingResponseCode.OK && pr.purchasesList.isNotEmpty()) {
//                        if(pr.purchasesList.size != 1) {
//                            Log.w("BillingViewModel", "Strange: more than one purchases made, skipping all of them..")
//                        }
//                        else {
//                            val purchase = pr.purchasesList[0]
//                            if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//                                if(billingManager.acknowlidgePurchase(pr.purchasesList[0])) {
//                                    Repository.getRepository().preferences.updatePaidVersionStatus(
//                                        PaidVersionStatus.Paid)
//                                }
//                            }
//                            else if(purchase.purchaseState == Purchase.PurchaseState.PENDING) {
//                                Repository.getRepository().preferences.updatePaidVersionStatus(
//                                    PaidVersionStatus.Pending)
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}