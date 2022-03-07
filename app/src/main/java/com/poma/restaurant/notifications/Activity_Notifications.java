package com.poma.restaurant.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.poma.restaurant.R;
import com.poma.restaurant.menu.Activity_Menu;

public class Activity_Notifications extends AppCompatActivity {
    private static final String TAG_LOG = Activity_Menu.class.getName();
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        this.btn_logout= (Button)findViewById(R.id.btn_logout);


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcast_logout(v);
            }
        });

    }


    private void broadcast_logout(View view){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        Intent intent = new Intent();
        intent.setAction("com.poma.restaurant.broadcastreceiversandintents.BROADCAST_LOGOUT");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
        Log.d(TAG_LOG, "Broadcast mandato");
    }
}