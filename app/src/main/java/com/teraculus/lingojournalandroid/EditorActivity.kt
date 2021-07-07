package com.teraculus.lingojournalandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.PaidVersionStatus
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.ui.components.EditActivityContent
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController
import com.teraculus.lingojournalandroid.viewmodel.EditActivityViewModelFactory

private const val KEY_ARG_EDITOR_ACTIVITY_ID = "KEY_ARG_EDITOR_ACTIVITY_ID"
private const val KEY_ARG_EDITOR_FROM_GOAL_ACTIVITY_ID = "KEY_ARG_EDITOR_FROM_GOAL_ACTIVITY_ID"
private const val AD_UNIT_ID = "ca-app-pub-5945698753650975/2437759523"
private const val AD_UNIT_TEST_ID = "ca-app-pub-3940256099942544/1033173712"

fun launchEditorActivity(context: Context, id: String?, goalId: String? = null) {
    context.startActivity(createEditorActivityIntent(context, id, goalId))
}

fun createEditorActivityIntent(context: Context, id: String?, goalId: String?): Intent {
    val intent = Intent(context, EditorActivity::class.java)
    intent.putExtra(KEY_ARG_EDITOR_ACTIVITY_ID, id)
    intent.putExtra(KEY_ARG_EDITOR_FROM_GOAL_ACTIVITY_ID, goalId)
    return intent
}

data class EditorActivityArg(
    val id: String?,
    val goalId: String?
)

class EditorActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null
    private var TAG = "EditorActivity"
    lateinit var modelFactory : EditActivityViewModelFactory
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = getDetailsArgs(intent)

        PickerProvider.getPickerProvider().fragmentManagerProvider = { supportFragmentManager }
        modelFactory = EditActivityViewModelFactory(args.id, args.goalId, PickerProvider.getPickerProvider())
        val freeVersion = Repository.getRepository().preferences.all().value?.paidVersionStatus != PaidVersionStatus.Paid

        // only loadAd if it's a new activity and it's free version
        if(freeVersion && args.id == null ) {
            loadAd()
        }

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme {
                    EditActivityContent(
                            onDismiss = { success ->
                                if(success)
                                    showAd()
                                else
                                    onBackPressed()
                            },
                            model = viewModel(key = "editActivityViewModel",
                            factory = modelFactory))
                }
            }
        }
    }

    private fun showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    onBackPressed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                    onBackPressed()
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
            onBackPressed()
        }
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
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

    override fun onBackPressed() {
        if (isTaskRoot) {
            val parentIntent = Intent(this, MainActivity::class.java)
            startActivity(parentIntent)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun getDetailsArgs(intent: Intent): EditorActivityArg {
        val id = intent.getStringExtra(KEY_ARG_EDITOR_ACTIVITY_ID)
        val goalId = intent.getStringExtra(KEY_ARG_EDITOR_FROM_GOAL_ACTIVITY_ID)
        return EditorActivityArg(id,goalId)
    }
}