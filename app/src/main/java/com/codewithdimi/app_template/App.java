package com.codewithdimi.ankinudge;

import android.app.Application;

import com.codewithdimi.ankinudge.data.SharedPreferenceProvider;

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
        //ads
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
