package com.poma.restaurant.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String detectedAction = intent.getAction();
        if(detectedAction.equals("com.devis_android.broadcastreceiversandintents.SendBroadcast"))
            Toast.makeText(context, "Broadcast Intent detected.", Toast.LENGTH_LONG).show();
        else if(detectedAction.equals("android.intent.action.ACTION_POWER_DISCONNECTED"))
            Toast.makeText(context, "External power disconnected", Toast.LENGTH_LONG).show();
    }
}
