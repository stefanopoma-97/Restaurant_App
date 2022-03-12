package com.poma.restaurant.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.databinding.ActivityNotificationsAdminBinding;
import com.poma.restaurant.databinding.ActivityNotificationsBinding;
import com.poma.restaurant.menu.Activity_Drawer_Menu_Admin;
import com.poma.restaurant.menu.Activity_Drawer_Menu_User;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.RecyclerViewAdapter.OnNotificationClickListener;
import com.poma.restaurant.model.User;

import java.util.Map;

public class Activity_Notifications_Admin extends Activity_Drawer_Menu_Admin implements Fragment_Notification_List.NotificationListInterface, OnNotificationClickListener, Fragment_Notification.NotificationInterface {
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

    ActivityNotificationsAdminBinding activityNotificationsAdminBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_notifications_admin);

        //Menu laterale
        activityNotificationsAdminBinding = ActivityNotificationsAdminBinding.inflate(getLayoutInflater());
        setContentView(activityNotificationsAdminBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.notification_title));


        Log.d(TAG_LOG, "on create");

        this.fragment_notification_list = (Fragment_Notification_List)
                getSupportFragmentManager().findFragmentById(R.id.fragment_notification_list_admin);

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

    private void check_user(){
        Boolean anonymous_f = false;
        Boolean anonymous_s = false;
        Boolean anonymous = false;
        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        //Login Firestore
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Log.d(TAG_LOG, "Non trovato utente con Firestore");
            finish();
        }

        //Login Shared Preferences
        this.currentUser2 = User.load(this);
        if (currentUser2 == null) {
            Log.d(TAG_LOG, "Non trovato utente con Firestore");
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
                                Log.d(TAG_LOG, "Utente admin");
                            }
                            else {
                                Log.d(TAG_LOG, "Errore - si tratta di un utente non admin");
                                finish();
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

    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        finish();
    }

    @Override
    public void onNotificationClick(Notification n) {
        boolean dual_pane = getResources().getBoolean(R.bool.dual_pane);
        if (dual_pane==false){
            Log.d(TAG_LOG, "lister su activity, fa partire intent");
            final Intent intent = new Intent(Activity_Notifications_Admin.this, Activity_Notification.class);
            intent.putExtra(NOTIFICATION_ID_EXTRA, n.getId());
            startActivity(intent);
        }
        else{
            this.fragment_notification = (Fragment_Notification)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_notification_land_admin);
            this.fragment_notification.setNotification_id(n.getId());
        }
    }

    @Override
    public void goBack() {
        Log.d(TAG_LOG, "Go Back");
        this.fragment_notification = (Fragment_Notification)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_notification_land_admin);
        this.fragment_notification.clearFragment();
    }

    @Override
    public void view(Notification n) {
        redirect_to_notification(n);
        finish();
    }

    @Override
    public void click_notification(String id) {

    }

    @Override
    public void cancel() {

    }

    private void redirect_to_notification(Notification n){
        //TODO in base al tipo di notifica vanno creati intent differenti
        final Intent intent = new Intent(Activity_Notifications_Admin.this, Activity_Menu.class);
        intent.putExtra(USEFUL_ID_EXTRA, n.getUseful_id());
        startActivity(intent);

    }
}