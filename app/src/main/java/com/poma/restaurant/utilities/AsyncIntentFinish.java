package com.poma.restaurant.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.poma.restaurant.menu.Activity_Menu_Anonymous;

public class AsyncIntentFinish extends AsyncTask<Object,Void,Void> {

    @Override
    protected Void doInBackground(Object... objects) {
        Activity_Menu_Anonymous c = (Activity_Menu_Anonymous) objects[0];
        SystemClock.sleep(900);
        c.finish();

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