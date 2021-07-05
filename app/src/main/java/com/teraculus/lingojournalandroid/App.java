package com.teraculus.lingojournalandroid;

import android.app.Application;

import io.realm.Realm;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        // Initialize the Mobile Ads SDK.
//        MobileAds.initialize(this, initializationStatus -> {
//            // Set your test devices. Check your logcat output for the hashed device ID to
//            // get test ads on a physical device. e.g.
//            // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
//            // to get test ads on this device."
//            List<String> testDeviceIds = Arrays.asList("C3B9A2FC8DCF3291516F3FB843E4A000");
//            RequestConfiguration configuration =
//                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//            MobileAds.setRequestConfiguration(configuration);
//        });
    }
}
