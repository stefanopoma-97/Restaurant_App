package com.poma.restaurant.notifications;

import androidx.annotation.NonNull;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.databinding.ActivityNotificationsBinding;
import com.poma.restaurant.menu.Activity_Drawer_Menu_User;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.RecyclerViewAdapter.OnNotificationClickListener;
import com.poma.restaurant.model.User;
import com.poma.restaurant.restaurant.Activity_Restaurant_Admin;
import com.poma.restaurant.restaurant.Activity_Restaurant_Client;
import com.poma.restaurant.utilities.Action;

import java.util.Map;

public class Activity_Notifications extends Activity_Drawer_Menu_User implements Fragment_Notification_List.NotificationListInterface, OnNotificationClickListener, Fragment_Notification.NotificationInterface {
    private static final String TAG_LOG = Activity_Menu.class.getName();
    private Button btn_logout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;

    private static final String NOTIFICATION_ID_EXTRA = "com.poma.restaurant.NOTIFICATION_ID_EXTRA";
    private static final String USEFUL_ID_EXTRA = "com.poma.restaurant.USEFUL_ID_EXTRA";


    private BroadcastReceiver broadcastReceiver;

    private static Fragment_Notification_List fragment_notification_list;
    private static Fragment_Notification fragment_notification;


    //Menu laterale
    ActivityNotificationsBinding activityNotificationsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_notifications);



        //Menu laterale
        activityNotificationsBinding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(activityNotificationsBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.notification_title));


        Log.d(TAG_LOG, "on create");

        this.fragment_notification_list = (Fragment_Notification_List)
                getSupportFragmentManager().findFragmentById(R.id.fragment_notification_list);

        this.mAuth= FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();

        ///Riceve broadcast
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.poma.restaurant.broadcastreceiversandintents.BROADCAST_LOGOUT");
        this.broadcastReceiver = new Receiver(new Broadcast_receiver_callBack_logout() {
            @Override
            public void onCallBack() {
                Log.d(TAG_LOG, "Receiver onCallBack");
                logout();
            }
        });
        registerReceiver(this.broadcastReceiver, intentFilter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        check_user();
    }



    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        finish();
    }

    //controllo la presenza di un utente loggato.
    private void check_user(){

        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        //Login Firestore
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "Trovato utente con Firestore, id: "+this.currentUser.getUid());
        }
        else {
            Log.d(TAG_LOG, "Non trovato utente con Firestore");
            finish();
        }

        //Login Shared Preferences
        this.currentUser2 = User.load(this);
        if (currentUser2 != null) {
            Log.d(TAG_LOG, "Trovato utente con Shared preferences, id: "+this.currentUser2.getID());
        }
        else {
            Log.d(TAG_LOG, "Non trovato utente con SharedPreference");
            finish();
        }


        if (currentUser.getUid().equals(currentUser2.getID())){
            Log.d(TAG_LOG, "Gli utenti coincidono");
            this.db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();

                            if((boolean)data.get("admin")){
                                Log.d(TAG_LOG, "L'utente ?? admin");
                                finish();
                            }
                            else {
                                Log.d(TAG_LOG, "L'utente ?? visitatore");
                            }
                        }
                    }
                }
            });
        }
        else {
            Log.d(TAG_LOG, "Errore - gli utenti non coincidono");
            finish();
        }

    }





    //Intefaccia frament notifications list
    @Override
    public void click_notification(String id) {

    }

    @Override
    public void cancel() {

    }

    //in ascolto sul click su una notifica
    @Override
    public void onNotificationClick(Notification n) {

        boolean dual_pane = getResources().getBoolean(R.bool.dual_pane);
        if (dual_pane==false){
            Log.d(TAG_LOG, "lister su activity, fa partire intent");
            final Intent intent = new Intent(Activity_Notifications.this, Activity_Notification.class);
            intent.putExtra(NOTIFICATION_ID_EXTRA, n.getId());
            startActivity(intent);
        }
        else{
            this.fragment_notification = (Fragment_Notification)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_notification_land);
            this.fragment_notification.setNotification_id(n.getId());
        }

    }


    //Interfaccia fragment singola notifica
    public void goBack() {
        Log.d(TAG_LOG, "Go Back");
        this.fragment_notification = (Fragment_Notification)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_notification_land);
        this.fragment_notification.clearFragment();
    }

    @Override
    public void view(Notification n) {
        redirect_to_notification(n);
        finish();
    }

    private void redirect_to_notification(Notification n){
        if (n.getType().equals(getResources().getString(R.string.new_restaurant))){
            Log.d(TAG_LOG, "Creando una notifica di tipo NUOVO RISTORANTE");
            Intent intent = new Intent(Activity_Notifications.this, Activity_Restaurant_Client.class);
            intent.putExtra(Action.RESTAURANT_ID_EXTRA, n.getUseful_id());
            Log.d(TAG_LOG, "Inserisco extra: "+Action.RESTAURANT_ID_EXTRA+" - "+n.getUseful_id());
            startActivity(intent);

        }
        else if(n.getType().equals(getResources().getString(R.string.new_favourite))) {
            Log.d(TAG_LOG, "Creando una notifica di tipo NUOVO PREFERITO");
            Intent intent2 = new Intent(Activity_Notifications.this, Activity_Restaurant_Admin.class);
            intent2.putExtra(Action.RESTAURANT_ID_EXTRA, n.getUseful_id());
            Log.d(TAG_LOG, "Inserisco extra: "+Action.RESTAURANT_ID_EXTRA+" - "+n.getUseful_id());
            startActivity(intent2);
        }

    }
}