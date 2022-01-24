package com.codewithdimi.ankinudge.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BillingManager(context: Context) {

    // Channel to receive PurchaseResult
    private val purchaseChannel: Channel<PurchasesResult> = Channel(Channel.UNLIMITED)

    private var billingClient = BillingClient.newBuilder(context)
        .setListener { responseCode, purchases ->
            purchaseChannel.trySend(PurchasesResult(responseCode, purchases.orEmpty())).isSuccess
        }
        .enablePendingPurchases()
        .build()

    private val billingConnectionMutex = Mutex()

    private val resultAlreadyConnected = BillingResult.newBuilder()
        .setResponseCode(BillingClient.BillingResponseCode.OK)
        .setDebugMessage("Billing client is already connected")
        .build()

    private val resultSkuUnavailable = BillingResult.newBuilder()
        .setResponseCode(BillingClient.BillingResponseCode.ERROR)
        .setDebugMessage("SKU unavailable")
        .build()


    suspend fun ensureConnected(): Boolean {
        return billingClient.connect().responseCode == BillingClient.BillingResponseCode.OK
    }

    /**
     * Returns immediately if this BillingClient is already connected, otherwise
     * initiates the connection and suspends until this client is connected.
     * If a connection is already in the process of being established, this
     * method just suspends until the billing client is ready.
     */
    suspend fun BillingClient.connect(): BillingResult = billingConnectionMutex.withLock {
        if (isReady) {
            // fast path: avoid suspension if already connected
            resultAlreadyConnected
        } else {
            unsafeConnect()
        }
    }

    suspend fun BillingClient.unsafeConnect() = suspendCoroutine<BillingResult> { cont ->
        startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                cont.resume(billingResult)
            }
            override fun onBillingServiceDisconnected() {
                // no need to setup reconnection logic here, call ensureReady()
                // before each purchase to reconnect as necessary
            }
        })
    }

    suspend fun querySkuDetails(): SkuDetails? {
        // wait for connection
        ensureConnected()

        val skuList = ArrayList<String>()
        skuList.add(PAID_VERSION_SKU)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        // leverage querySkuDetails Kotlin extension function
        val skuDetailsResult = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params.build())
        }

        // Process the result.
        return skuDetailsResult.skuDetailsList.orEmpty().find { skuDetails -> skuDetails.sku == "paid_version"  }
    }

    suspend fun queryPurchases(): PurchasesResult {
        // wait for connection
        ensureConnected()
        return withContext(Dispatchers.IO) {
            return@withContext billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP)
        }
    }

    suspend fun acknowlidgePurchase(purchase: Purchase): Boolean {
        // wait for connection
        ensureConnected()

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

    suspend fun alreadyAcknowledged(): Boolean {
        ensureConnected()
        val purchases = queryPurchases()
        return purchases.purchasesList.isNotEmpty() && purchases.purchasesList.all { it.isAcknowledged }
    }

    suspend fun getPurchasePendingAcknowledge(): Purchase? {
        ensureConnected()
        val purchases = queryPurchases()
        return purchases.purchasesList.find { it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
    }

    suspend fun launchBillingFlow(context: Activity): PurchasesResult? {
        // wait for connection
        ensureConnected()

        val skuDetail = querySkuDetails()
        if(skuDetail == null) {
            Log.i("BillingManager", "SKU details unavailable")
            return null
        }

        val params = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetail)
            .build()

        billingClient.launchBillingFlow(context, params)
        return purchaseChannel.receive()
    }

    companion object {
        private var INSTANCE: BillingManager? = null

        fun init(context: Context) {
            synchronized(BillingManager::class) {
                INSTANCE = BillingManager(context)
            }
        }

        fun getManager(): BillingManager {
            return synchronized(BillingManager::class) {
                INSTANCE!!
            }
        }

        const val PAID_VERSION_SKU = "paid_version"
    }
}