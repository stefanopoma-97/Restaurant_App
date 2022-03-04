package com.poma.restaurant.utilities;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        com.poma.restaurant.utilities.MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return com.poma.restaurant.utilities.MyApplication.context;
    }
}
