package com.teraculus.lingojournalandroid

import android.os.Bundle
import androidx.activity.compose.setContent
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
import com.teraculus.lingojournalandroid.data.BillingManager
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.PaidVersionStatus
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.ui.MainContent
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PickerProvider.getPickerProvider().fragmentManagerProvider = { supportFragmentManager }

        validatePurchaseStatus()

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme {
                    MainContent(
                        onActivityClick = { launchDetailsActivity(this, it) },
                        onAddActivity = { launchEditorActivity(this, null) },
                        onOpenSettings = { launchSettingsActivity(this) },
                        onOpenStats = { launchStatsActivity(this) },
                        onOpenGoals = { launchGoalsActivity(this) },
                        onGoalClick = { launchEditorActivity(this, null, it) }
                    )
                }
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
}