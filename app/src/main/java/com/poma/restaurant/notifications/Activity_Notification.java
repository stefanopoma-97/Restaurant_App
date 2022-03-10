package com.poma.restaurant.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.User;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Map;

public class Activity_Notification extends AppCompatActivity implements Fragment_Notification.NotificationInterface {
    private static final String NOTIFICATION_ID_EXTRA = "com.poma.restaurant.NOTIFICATION_ID_EXTRA";
    private static final String TAG_LOG = Activity_Notification.class.getName();

    private static final String USEFUL_ID_EXTRA = "com.poma.restaurant.USEFUL_ID_EXTRA";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private BroadcastReceiver broadcastReceiver;


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


        this.imageView_back = (ImageView)findViewById(R.id.arrow_back_notification);
        this.imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        check_user();
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

    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        finish();
    }

    private void check_user(){
        Boolean anonymous_f = false;
        Boolean anonymous_s = false;
        Boolean anonymous = false;
        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");

        //Login Firestore
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "Trovato utente con Firestore, id: "+this.currentUser.getUid());
        }
        else {
            Log.d(TAG_LOG, "Non trovato utente con Firestore");
            anonymous_f = true;
        }

        //Login Shared Preferences
        this.currentUser2 = User.load(this);
        if (currentUser2 != null) {
            Log.d(TAG_LOG, "Trovato utente con Shared preferences, id: "+this.currentUser2.getID());
        }
        else {
            Log.d(TAG_LOG, "Non trovato utente con SharedPreference");
            anonymous_s = true;
        }

        //anonimo, user, admin o errore
        if (anonymous_f | anonymous_s){
            Log.d(TAG_LOG, "ERRORE - Non c'è utente, accesso anonimo");
            finish();
        }
        else if (anonymous_f!=anonymous_s){
            Log.d(TAG_LOG, "ERRORE - ho trovato solo un utente");
            finish();
        }
        else if (this.currentUser.getUid().equals(this.currentUser2.getID())){
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
                                Log.d(TAG_LOG, "Utente Admin");
                            }
                            else {
                                Log.d(TAG_LOG, "Utente Visitatore");
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

}