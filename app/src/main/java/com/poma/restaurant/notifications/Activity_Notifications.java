package com.poma.restaurant.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.poma.restaurant.R;
import com.poma.restaurant.login.Fragment_Login;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.RecyclerViewAdapter.RecyclerViewAdapter_Notification;

import java.util.ArrayList;

public class Activity_Notifications extends AppCompatActivity implements Fragment_Notification_List.NotificationListInterface {
    private static final String TAG_LOG = Activity_Menu.class.getName();
    private Button btn_logout;
    private RecyclerView rv;
    private ArrayList<Notification> mdata;

    private static Fragment_Notification_List fragment_notification_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Log.d(TAG_LOG, "on create");

        this.fragment_notification_list = (Fragment_Notification_List)
                getSupportFragmentManager().findFragmentById(R.id.fragment_notification_list);


        //RV
        rv = findViewById(R.id.RV_notification);

        // here we have created new array list and added data to it.
        mdata = new ArrayList<>();

        //notifica 1
        for (int i = 0; i<100; i++){
            Notification n1 = new Notification("userid", "id", "Titolo not 1");
            n1.setContent("Descrizione delle notifica 1");
            long l = new Long(8407);
            n1.setDate(l);
            mdata.add(n1);
        }



        // we are initializing our adapter class and passing our arraylist to it.
        RecyclerViewAdapter_Notification adapter = new RecyclerViewAdapter_Notification(this, mdata);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);




    }


    private void broadcast_logout(View view){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        Intent intent = new Intent();
        intent.setAction("com.poma.restaurant.broadcastreceiversandintents.BROADCAST_LOGOUT");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
        Log.d(TAG_LOG, "Broadcast mandato");
    }

    @Override
    public void click_notification(String id) {

    }

    @Override
    public void cancel() {

    }
}