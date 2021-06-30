package com.teraculus.lingojournalandroid.ui.ads

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.teraculus.lingojournalandroid.BuildConfig
import kotlin.math.roundToInt

private const val AD_UNIT_ID = "ca-app-pub-5945698753650975/1919078294"
private const val AD_UNIT_TEST_ID = "ca-app-pub-3940256099942544/6300978111"

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    Card(modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        val context = LocalContext.current.applicationContext
        BoxWithConstraints() {
            AndroidView(::AdView, update = {
                    view ->
                view.adUnitId = if (BuildConfig.DEBUG) AD_UNIT_TEST_ID else AD_UNIT_ID
                view.adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize (context, maxWidth.value.roundToInt())
                val adRequest = AdRequest.Builder().build()
                view.loadAd(adRequest)
            })
        }
    }
}