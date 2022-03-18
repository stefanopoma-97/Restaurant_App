package com.poma.restaurant.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class AsyncIntent extends AsyncTask<Object,Void,Void> {

    @Override
    protected Void doInBackground(Object... objects) {
        Context c = (Context) objects[1];
        Intent i = (Intent) objects[0];
        SystemClock.sleep(900);
        c.startActivity(i);

        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        //Toast.makeText(MyApplication.getAppContext(), "Done ASYNC!", Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onProgressUpdate(Void... item) {

    }
}