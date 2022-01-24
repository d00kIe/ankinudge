//package com.codewithdimi.ankinudge.ui.ads
//
//import android.app.Activity
//import android.content.Context
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Card
//import androidx.compose.material.LinearProgressIndicator
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.google.android.gms.ads.AdListener
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdSize
//import com.google.android.gms.ads.AdView
//import com.codewithdimi.ankinudge.ui.components.Label
//import com.codewithdimi.ankinudge.viewmodel.BillingViewModel
//
//private const val AD_UNIT_ID = "ca-app-pub-5945698753650975/1919078294"
//private const val AD_UNIT_TEST_ID = "ca-app-pub-3940256099942544/6300978111"
//
//class Listener(val onLoaded: () -> Unit): AdListener() {
//    override fun onAdLoaded() {
//        onLoaded()
//    }
//}
//
//@Composable
//fun AdBanner(modifier: Modifier = Modifier, state: AdViewState, billingModel: BillingViewModel = viewModel(key = "billingViewModel")) {
//    val loaded by state.loaded
//    Card(modifier = modifier,
//        shape = RoundedCornerShape(16.dp)
//    ) {
//        val activity = LocalContext.current as Activity
//        Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)) {
//            if(!loaded) {
//                Label(text = "Loading ad...", modifier = Modifier.padding(bottom = 16.dp))
//                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//            }
//            BoxWithConstraints() {
//                AndroidView(
//                    factory = {
//                        state.adView
//                    },
//                    update = { })
//            }
//            if (loaded) {
//                Row(horizontalArrangement = Arrangement.End, modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(end = 8.dp, top = 8.dp)) {
//                    Text(text = "Remove Ads", modifier = Modifier.clickable { billingModel.tryPurchase(activity) }, style = MaterialTheme.typography.caption)
//                }
//            }
//        }
//    }
//}
//
//class AdViewState(val context: Context, adRequest: AdRequest, width: Int) {
//    var loaded = mutableStateOf(false)
//    private val listener = Listener { loaded.value = true }
//    val adView = AdView(context).apply {
//        adListener = listener
//        if (adUnitId.isNullOrEmpty()) {
//            adUnitId =
//                AD_UNIT_ID //if (BuildConfig.DEBUG) AD_UNIT_TEST_ID else AD_UNIT_ID
//            adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
//                context,
//                width
//            ) // 64 for padding
//
//            loadAd(adRequest)
//        }
//    }
//}
//
//@Composable
//fun rememberAdViewState(adRequest: AdRequest, width: Int): AdViewState {
//    val context = LocalContext.current
//    return remember(width) {
//        AdViewState(context, adRequest, width)
//    }
//}