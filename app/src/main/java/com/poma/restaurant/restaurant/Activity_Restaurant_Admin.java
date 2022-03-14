package com.poma.restaurant.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.account.Activity_Account;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

import java.util.Map;

public class Activity_Restaurant_Admin extends AppCompatActivity implements Fragment_Restaurant_Client.RestaurantInterface {

    private static final String TAG_LOG = Activity_Restaurant_Admin.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;

    private static final int LOAD_IMAGE_REQUEST_ID = 5;

    private BroadcastReceiver broadcastReceiver;

    private static Fragment_Restaurant_Client fragment_restaurant_client;
    private ImageView imageView_back;

    private static final String RESTAURANT_ID_EXTRA = "com.poma.restaurant.RESTAURANT_ID_EXTRA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "on create");
        setContentView(R.layout.activity_restaurant_admin);

        this.mAuth= FirebaseAuth.getInstance();
        this.fragment_restaurant_client = (Fragment_Restaurant_Client)
                getSupportFragmentManager().findFragmentById(R.id.fragment_restaurant_admin);

        Intent intent = getIntent(); //receive the intent from Activity_first_access
        String id_restaurant= intent.getStringExtra(RESTAURANT_ID_EXTRA);
        Log.d(TAG_LOG, "Ricavo il seguente ID del ristorante: "+id_restaurant);
        this.fragment_restaurant_client.setAdmin();
        this.fragment_restaurant_client.setRestaurant_id(id_restaurant);
        this.fragment_restaurant_client.setNotFavouriteAccess();

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


        this.imageView_back = (ImageView)findViewById(R.id.arrow_back_restaurant_admin);
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
        Log.d(TAG_LOG, "on start");
        check_user_admin();
    }


    public void back() {
        Log.d(TAG_LOG, "Back");
        finish();
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
    public void goBack() {
        finish();
    }

    @Override
    public void edit_restaurant(String restaurant_id) {
        Intent intent = new Intent(Activity_Restaurant_Admin.this, Activity_Edit_Restaurant.class);
        intent.putExtra(Action.RESTAURANT_ID_EXTRA, restaurant_id);
        startActivity(intent);

    }


}