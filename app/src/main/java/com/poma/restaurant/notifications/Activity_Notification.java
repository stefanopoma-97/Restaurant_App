package com.poma.restaurant.notifications;

import androidx.appcompat.app.AppCompatActivity;
import com.poma.restaurant.R;
import com.poma.restaurant.menu.Activity_Menu;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Activity_Notification extends AppCompatActivity implements Fragment_Notification.NotificationInterface {
    private static final String NOTIFICATION_ID_EXTRA = "com.poma.restaurant.NOTIFICATION_ID_EXTRA";
    private static final String TAG_LOG = Activity_Notification.class.getName();

    private static Fragment_Notification fragment_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Log.d(TAG_LOG, "on create");
        this.fragment_notification = (Fragment_Notification)
                getSupportFragmentManager().findFragmentById(R.id.fragment_notification);

        Intent intent = getIntent(); //receive the intent from Activity_first_access
        String id_notifica= intent.getStringExtra(NOTIFICATION_ID_EXTRA);
        Log.d(TAG_LOG, "Ricavo il seguente ID della notifica: "+id_notifica);
        this.fragment_notification.setNotification_id(id_notifica);


    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void view() {

    }
}