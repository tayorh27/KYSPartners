package com.kys.kyspartners;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.devs.acr.AutoErrorReporter;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.kys.kyspartners.Database.AppData;
import com.kys.kyspartners.Database.DatabaseDb;
import com.kys.kyspartners.Information.User;
import com.zendesk.logger.Logger;
import com.zendesk.sdk.model.access.AnonymousIdentity;
import com.zendesk.sdk.network.impl.ZendeskConfig;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by sanniAdewale on 11/05/2017.
 */

public class MyApplication extends Application {
    private static MyApplication sInstance;
    private String TASK_TAG_PERIODIC = "myTask";
    AppData data;
    User user;

    private static DatabaseDb database;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Fabric.with(this, new Crashlytics());
        data = new AppData(getAppContext());
        user = data.getUser();
        logUser();
        Logger.setLoggable(BuildConfig.DEBUG);
        initializeZendesk();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("avenir_light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        //MyTask();
        database = new DatabaseDb(this);
//        AutoErrorReporter.get(this)
//                .setEmailAddresses("gisanrinadetayo@gmail.com")
//                .setEmailSubject("Auto Crash Report")
//                .start();
    }

    private void MyTask() {
        GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(getAppContext());
        PeriodicTask task = new PeriodicTask.Builder()
                //.setService(MyLocationService.class)
                .setTag(TASK_TAG_PERIODIC)
                .setPeriod(300L)
                .build();
        gcmNetworkManager.schedule(task);
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public synchronized static DatabaseDb getWritableDatabase() {
        if (database == null) {
            database = new DatabaseDb(getAppContext());
        }
        return database;

    }

    private void initializeZendesk() {
        // Initialize the Support SDK with your Zendesk Support subdomain, mobile SDK app ID, and client ID.
        // Get these details from your Zendesk Support dashboard: Admin -> Channels -> Mobile SDK
        ZendeskConfig.INSTANCE.init(getApplicationContext(),
                getString(R.string.com_zendesk_sdk_url),
                getString(R.string.com_zendesk_sdk_identifier),
                getString(R.string.com_zendesk_sdk_clientIdentifier));


        // Authenticate anonymously as a Zendesk Support user
        ZendeskConfig.INSTANCE.setIdentity(
                new AnonymousIdentity.Builder()
                        .withNameIdentifier(user.username)
                        .withEmailIdentifier(user.email)
                        .build()
        );
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(user.access_code);
        Crashlytics.setUserEmail(user.email);
        Crashlytics.setUserName(user.username);
    }


}
