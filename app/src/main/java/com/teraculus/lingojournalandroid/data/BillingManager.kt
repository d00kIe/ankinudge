package com.teraculus.lingojournalandroid.data

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.teraculus.lingojournalandroid.model.PaidVersionStatus
import kotlinx.coroutines.*

enum class BillingStatus {
    Initial,
    QueryingSKU,
    Ready,
    Disconnected,
}

class BillingManager(context: Context, repository: Repository) {
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            handlePurchaseResponse(billingResult, purchases, repository)
        }

    private val purchasesResponseListener =
        PurchasesResponseListener { billingResult, purchases ->
            handlePurchaseResponse(billingResult, purchases, repository)
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    private val _status = MutableLiveData<BillingStatus>(BillingStatus.Initial)
    private val _paidVersionSku = MutableLiveData<SkuDetails>(null)
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    val canLaunchBillingFlow = MediatorLiveData<Boolean>().apply {
        fun update() {
            value = _status.value == BillingStatus.Ready && _paidVersionSku.value != null
        }

        addSource(_status) { update() }
        addSource(_paidVersionSku) { update() }

        update()
    }

    val stateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // The BillingClient is ready. You can query purchases here.
                scope.launch {
                    _paidVersionSku.value = querySkuDetails()
                }
                // query purchises
                billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, purchasesResponseListener)
            }
        }

        override fun onBillingServiceDisconnected() {
            _status.value = BillingStatus.Disconnected
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
        }
    }

    init {
        startConnection()
    }


    private fun handlePurchaseResponse(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
        repository: Repository
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    scope.launch {
                        if (acknowlidgePurchase(purchase)) {
                            repository.preferences.updatePaidVersionStatus(PaidVersionStatus.Paid)
                        }
                    }
                } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                    // TODO
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i("BillingManager", "User cancelled purchase")
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }

    private fun startConnection() {
        _status.value = BillingStatus.Initial
        billingClient.startConnection(stateListener)
    }

    private suspend fun querySkuDetails(): SkuDetails? {
        _status.value = BillingStatus.QueryingSKU
        val skuList = ArrayList<String>()
        skuList.add("paid_version")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        // leverage querySkuDetails Kotlin extension function
        val skuDetailsResult = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params.build())
        }

        // Process the result.

        return skuDetailsResult.skuDetailsList.orEmpty().find { skuDetails -> skuDetails.sku == "paid_version"  }
    }

    private suspend fun acknowlidgePurchase(purchase: Purchase): Boolean {
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
            val ackPurchaseResult = withContext(Dispatchers.IO) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
            }

            return ackPurchaseResult.responseCode == BillingClient.BillingResponseCode.OK
        }
        return true
    }

    fun prepareForBillingFlow() {
        if(_status.value == BillingStatus.Disconnected) {
            startConnection()
        }
    }

    fun launchBillingFlow(context: Activity): Boolean {
        if(canLaunchBillingFlow.value != true) {
            Log.w("BillingManager", "User wants to purchase, but SKU is not available.")
            return false;
        }

        _paidVersionSku.value?.let { skuDetails ->
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
            val responseCode = billingClient.launchBillingFlow(context, flowParams).responseCode
            Log.i("BillingManager", "Launched billing flow")
            return responseCode == BillingClient.BillingResponseCode.OK
        }

        return false
    }
}