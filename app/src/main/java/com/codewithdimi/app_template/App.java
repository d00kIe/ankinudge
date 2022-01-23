package com.codewithdimi.app_template;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.codewithdimi.app_template.BuildConfig;
import com.codewithdimi.app_template.data.BillingManager;
import com.codewithdimi.app_template.data.ConsentManager;
import com.codewithdimi.app_template.data.SharedPreferenceProvider;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        // Shared preferences provider
        SharedPreferenceProvider.Companion.init(this);

        // Only activate analytics if we have consent
        if(!BuildConfig.DEBUG) {
//            ConsentManager manager = new ConsentManager();
//            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(manager.hasConsent());
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(manager.hasConsent());
        }

        // Initialize billing
//        BillingManager.Companion.init(this);
//
//        Context context = this;
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                // Initialize the Mobile Ads SDK.
//                MobileAds.initialize(context, initializationStatus -> {
//                    List<String> testDeviceIds = Arrays.asList("C3B9A2FC8DCF3291516F3FB843E4A000");
//                    RequestConfiguration configuration =
//                            new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//                    MobileAds.setRequestConfiguration(configuration);
//                    MobileAds.setAppMuted(true);
//                });
//            }
//        };
//        thread.start();
    }

}
