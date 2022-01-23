package com.codewithdimi.app_template.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codewithdimi.app_template.R
import com.codewithdimi.app_template.viewmodel.BillingViewModel
import com.codewithdimi.app_template.viewmodel.ConsentViewModel

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
    Card(Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
            Arrangement.Center
        ) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Shield, contentDescription = null, modifier = Modifier.size(72.dp))
                    Image(
                        painterResource(id = R.drawable.ic_welcome_icon),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp))
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text("We care about your privacy", style = MaterialTheme.typography.h5, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.size(32.dp))
            Text(text = "We use Google Firebase Analytics and Crashlytics to allow us to improve our application and provide you with the best service. Please read and agree to our privacy policy.")
            if(canPurchase == true) {
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "To keep the app free we need to use ads provided by Google Admob service. If you don't want to see ads, consider upgrading to Lingo Journal Pro.")
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