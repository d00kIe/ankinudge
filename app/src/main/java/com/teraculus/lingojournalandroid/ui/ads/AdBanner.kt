package com.teraculus.lingojournalandroid.ui.ads

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.teraculus.lingojournalandroid.viewmodel.BillingViewModel
import kotlin.math.roundToInt

private const val AD_UNIT_ID = "ca-app-pub-5945698753650975/1919078294"
private const val AD_UNIT_TEST_ID = "ca-app-pub-3940256099942544/6300978111"

private class Listener(val onLoaded: () -> Unit): AdListener() {
    override fun onAdLoaded() {
        onLoaded()
    }
}

@Composable
fun AdBanner(modifier: Modifier = Modifier, billingModel: BillingViewModel = viewModel(key = "billingViewModel")) {
    var loaded by remember { mutableStateOf(false)}
    Card(modifier = if(loaded) modifier else Modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        val context = LocalContext.current.applicationContext
        val activity = LocalContext.current as Activity
        Column(modifier = if (loaded) Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp) else Modifier) {
            BoxWithConstraints() {
                AndroidView(
                    factory = {
                        AdView(it).apply {
                            adListener = Listener { loaded = true }
                        }
                    },
                    update = { view ->
                        if (view.adUnitId.isNullOrEmpty()) {
                            view.adUnitId =
                                AD_UNIT_ID //if (BuildConfig.DEBUG) AD_UNIT_TEST_ID else AD_UNIT_ID
                            view.adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                                context,
                                maxWidth.value.roundToInt() - 64
                            ) // 64 for padding
                            val adRequest = AdRequest.Builder().build()
                            view.loadAd(adRequest)
                        }
                    })
            }
            if (loaded) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth().padding(end = 8.dp, top = 8.dp)) {
                    Text(text = "Remove Ads", modifier = Modifier.clickable { billingModel.tryPurchase(activity) }, style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}