package com.teraculus.lingojournalandroid

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.billingclient.api.BillingClient
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.teraculus.lingojournalandroid.data.BillingManager
import com.teraculus.lingojournalandroid.data.ConsentManager
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.PaidVersionStatus
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.ui.MainContent
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController
import kotlinx.coroutines.launch


private const val AD_UNIT_ID = "ad unit id"

class MainActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null
    private var TAG = "MainActivity"

    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PickerProvider.getPickerProvider().fragmentManagerProvider = { supportFragmentManager }

//        validatePurchaseStatus()
//
//        loadAndShowAds()

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme {
                    MainContent(
                        onOpenSettings = { launchSettingsActivity(this) }
                    )
                }
            }
        }
    }

    private fun loadAndShowAds() {
        // only loadAd if it's a new activity and it's free version and we have concent
        val freeVersion =
            Repository.getRepository().preferences.all().value?.paidVersionStatus != PaidVersionStatus.Paid
        if (freeVersion) {
            val manager = ConsentManager()
            if (manager.hasConsent() == true) {
                loadAd()
            }
        }

        // show add only if editor activity returns RESULT_FIRST_USER
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val isFree =
                    Repository.getRepository().preferences.all().value?.paidVersionStatus != PaidVersionStatus.Paid
                if (result.resultCode == RESULT_FIRST_USER && isFree) {
                    showAd()
                }
            }
    }

    private fun validatePurchaseStatus() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val repo = Repository.getRepository()
                val preferences = repo.preferences
                val paidVersionState = preferences.all().value?.paidVersionStatus
                val billing = BillingManager.getManager()

                // if we are in a pending state
                if (paidVersionState == PaidVersionStatus.Pending) {
                    val pendingAcknowledge = billing.getPurchasePendingAcknowledge()
                    pendingAcknowledge?.let {
                        if (billing.acknowlidgePurchase(pendingAcknowledge)) {
                            preferences.updatePaidVersionStatus(PaidVersionStatus.Paid)
                            return@repeatOnLifecycle
                        }
                    }
                }

                // TODO: Currently refunds are not working because google doesn't want to fix this issue https://issuetracker.google.com/issues/73982566
                val purchases = billing.queryPurchases()
                val retrievedPurchases = purchases.billingResult.responseCode == BillingClient.BillingResponseCode.OK
                if(retrievedPurchases) { //this is just to make sure we successfully retrieved something
                    val alreadyAcknowledged = billing.alreadyAcknowledged()
                    if (paidVersionState == PaidVersionStatus.Paid && !alreadyAcknowledged) {
                        preferences.updatePaidVersionStatus(PaidVersionStatus.Unknown) // refunded probably
                    } else if (paidVersionState != PaidVersionStatus.Paid && alreadyAcknowledged) {
                        preferences.updatePaidVersionStatus(PaidVersionStatus.Paid) // new installation on a new device probably
                    }
                }
            }
        }
    }

    private fun showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    mInterstitialAd = null
                    //onBackPressed()
                }
            }
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

    private fun loadAd() {
        val tl = Repository.getRepository().preferences.all().value?.languages?.firstOrNull()
        val adRequestBuilder = AdRequest.Builder().addKeyword("language learning")
        if(tl != null) {
            adRequestBuilder.addKeyword(getLanguageDisplayName(tl))
        }
        val adRequest = adRequestBuilder.build()

        InterstitialAd.load(this, AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }
}