package com.teraculus.lingojournalandroid;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.teraculus.lingojournalandroid.data.BillingManager;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        // Initialize billing
        BillingManager.Companion.init(this);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, initializationStatus -> {
            List<String> testDeviceIds = Arrays.asList("C3B9A2FC8DCF3291516F3FB843E4A000");
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);

            MobileAds.setAppMuted(true);
        });

    }
}
