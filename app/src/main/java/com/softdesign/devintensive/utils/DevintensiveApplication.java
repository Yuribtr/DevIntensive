package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DevintensiveApplication extends Application {
    public static SharedPreferences sSharedPreferences;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DevintensiveApplication.context = getApplicationContext();
        sSharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static Context getContext() {return DevintensiveApplication.context;}
}
