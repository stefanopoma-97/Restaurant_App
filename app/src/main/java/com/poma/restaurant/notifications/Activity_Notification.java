package com.poma.restaurant.notifications;

import androidx.appcompat.app.AppCompatActivity;
import com.poma.restaurant.R;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.Notification;


import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class Activity_Notification extends AppCompatActivity implements Fragment_Notification.NotificationInterface {
    private static final String NOTIFICATION_ID_EXTRA = "com.poma.restaurant.NOTIFICATION_ID_EXTRA";
    private static final String TAG_LOG = Activity_Notification.class.getName();

    private static final String USEFUL_ID_EXTRA = "com.poma.restaurant.USEFUL_ID_EXTRA";


    private static Fragment_Notification fragment_notification;

    private ImageView imageView_back;

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

        this.imageView_back = (ImageView)findViewById(R.id.arrow_back_notification);
        this.imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });


    }

    public void back() {
        Log.d(TAG_LOG, "Back");
        finish();
    }


    public void goBack() {
        Log.d(TAG_LOG, "Go Back");
        finish();
    }




    @Override
    public void view(Notification n) {
        redirect_to_notification(n);
        finish();
    }

    private void redirect_to_notification(Notification n){
        //TODO in base al tipo di notifica vanno creati intent differenti
        final Intent intent = new Intent(Activity_Notification.this, Activity_Menu.class);
        intent.putExtra(USEFUL_ID_EXTRA, n.getUseful_id());
        startActivity(intent);

    }

}