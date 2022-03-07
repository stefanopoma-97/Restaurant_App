package com.poma.restaurant.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {

    private Broadcast_receiver_callBack_logout listener;
    public Receiver() {
    }
    public Receiver(Broadcast_receiver_callBack_logout l) {
        this.listener = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String detectedAction = intent.getAction();
        Log.d("RECEIVER", "ho ricevuto un intent con action: "+intent.getAction());
        if(detectedAction.equals("com.poma.restaurant.broadcastreceiversandintents.BROADCAST_LOGOUT"))
            listener.onCallBack();

    }


}