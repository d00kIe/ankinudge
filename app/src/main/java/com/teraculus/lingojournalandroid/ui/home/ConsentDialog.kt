package com.teraculus.lingojournalandroid.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.viewmodel.BillingViewModel
import com.teraculus.lingojournalandroid.viewmodel.ConsentViewModel

@Composable
fun ConsentDialog(
    consentViewModel: ConsentViewModel = viewModel(key = "consentViewModel"),
    billingViewModel: BillingViewModel = viewModel(key = "billingViewModel")
) {
    val activity = LocalContext.current as Activity
    val hasConsent by consentViewModel.hasConsent.observeAsState()
    if(hasConsent == false) {
        Dialog(onDismissRequest = { }) {
            ConsentDialogContent(consentViewModel, billingViewModel, activity)
        }
    }
}

@Composable
fun ConsentDialogContent(
    consentViewModel: ConsentViewModel,
    billingViewModel: BillingViewModel,
    activity: Activity
) {
    val canPurchase by billingViewModel.canPurchase.observeAsState()
    var acceptedPrivacyPolicy by remember { mutableStateOf(false) }
    Card(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.Shield, contentDescription = null, modifier = Modifier.size(64.dp))
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text("We care about your privacy", style = MaterialTheme.typography.h5, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.size(32.dp))
            Text(text = "We use Google Firebase Analytics and Crashlytics to allow us to improve Lingo Journal and provide you with the best service. Please read and agree to our privacy policy.")
            if(canPurchase == true) {
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "To keep Lingo Journal free we need to use ads provided by Google Admob service. If you don't want to see ads, consider upgrading to Lingo Journal Pro.")
            }
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = { activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                Uri.parse("https://www.iubenda.com/privacy-policy/77822623"))
            ) }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(text = "Read privacy policy")
            }
            if(canPurchase != true) {
                Button(onClick = { consentViewModel.consent() }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(text = "Agree and continue")
                }
            } else {
                OutlinedButton(onClick = { consentViewModel.consent() }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(text = "Agree and continue with Ads")
                }
                Button(onClick = { consentViewModel.consent(); billingViewModel.tryPurchase(activity) }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(text = "Agree and upgrade to Pro")
                }
            }
        }
    }
}