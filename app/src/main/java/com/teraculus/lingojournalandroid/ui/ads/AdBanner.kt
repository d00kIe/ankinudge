package com.teraculus.lingojournalandroid.ui.ads

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlin.math.roundToInt

private const val AD_UNIT_ID = "ca-app-pub-5945698753650975/1919078294"
private const val AD_UNIT_TEST_ID = "ca-app-pub-3940256099942544/6300978111"

private class Listener(val onLoaded: () -> Unit): AdListener() {
    override fun onAdLoaded() {
        onLoaded()
    }
}
@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    var loaded by remember { mutableStateOf(false)}
    Card(modifier = if(loaded) modifier else Modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        val context = LocalContext.current.applicationContext
        BoxWithConstraints() {
            AndroidView(
                factory = {
                    AdView(it).apply {
                        adListener = Listener { loaded = true }
                    }
                },
                update = {
                    view ->
                    if(view.adUnitId.isNullOrEmpty()) {
                        view.adUnitId = AD_UNIT_ID //if (BuildConfig.DEBUG) AD_UNIT_TEST_ID else AD_UNIT_ID
                        view.adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize (context, maxWidth.value.roundToInt())
                        val adRequest = AdRequest.Builder().build()
                        view.loadAd(adRequest)
                    }
            })
        }
    }
}