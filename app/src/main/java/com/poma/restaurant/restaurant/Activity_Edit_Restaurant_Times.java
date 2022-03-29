package com.poma.restaurant.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.databinding.ActivityEditRestaurantBinding;
import com.poma.restaurant.databinding.ActivityEditRestaurantTimesBinding;
import com.poma.restaurant.menu.Activity_Drawer_Menu_Admin;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

import java.util.List;
import java.util.Map;

public class Activity_Edit_Restaurant_Times extends Activity_Drawer_Menu_Admin implements Fragment_Edit_Restaurant_Time.EditRestaurantTime {

    ActivityEditRestaurantTimesBinding activityEditRestaurantTimesBinding;

    private static final String TAG_LOG = Activity_Create_Restaurant.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;


    private BroadcastReceiver broadcastReceiver;

    Fragment_Edit_Restaurant_Time fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Menu laterale
        activityEditRestaurantTimesBinding = ActivityEditRestaurantTimesBinding.inflate(getLayoutInflater());
        setContentView(activityEditRestaurantTimesBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.edit_restaurant_times));

        this.mAuth= FirebaseAuth.getInstance();

        this.fragment = (Fragment_Edit_Restaurant_Time)
                getSupportFragmentManager().findFragmentById(R.id.fragment_edit_restaurant_time);

        //Imposta il fragment per la modifica

        /*
        Intent intent = getIntent(); //receive the intent from Activity_first_access
        String id_restaurant= intent.getStringExtra(Action.RESTAURANT_ID_EXTRA);
        this.fragment.setRestaurantID(id_restaurant);
        this.fragment.setUpdate();
         */

        //Riceve broadcast
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
        check_user_admin();
    }

    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        finish();
    }

    private void check_user_admin(){

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

    @Override
    public void edit_time(List<Boolean> days, boolean morning, boolean evening) {
        Log.d(TAG_LOG, "Salvataggio time");
        Log.d(TAG_LOG, "days: "+days);
        Log.d(TAG_LOG, "morning: "+morning);
        Log.d(TAG_LOG, "evening: "+evening);
    }

    @Override
    public void cancel() {
        finish();
    }
}