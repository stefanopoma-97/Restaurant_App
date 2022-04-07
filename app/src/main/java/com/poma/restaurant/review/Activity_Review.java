package com.poma.restaurant.review;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.User;
import com.poma.restaurant.restaurant.Activity_Restaurant_Client;
import com.poma.restaurant.restaurant.Fragment_Restaurant_Client;
import com.poma.restaurant.utilities.Action;

import java.util.Map;

public class Activity_Review extends AppCompatActivity {
    private static final String TAG_LOG = Activity_Review.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private String id_restaurant;

    private BroadcastReceiver broadcastReceiver;

    private static Fragment_Reviews_List fragment_reviews_list;

    ImageView imageView_back;
    AppCompatButton btn_create_review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "On create");
        setContentView(R.layout.activity_review);

        this.mAuth= FirebaseAuth.getInstance();
        this.fragment_reviews_list = (Fragment_Reviews_List)
                getSupportFragmentManager().findFragmentById(R.id.fragment_reviews_list);

        Intent intent = getIntent(); //receive the intent
        this.id_restaurant= intent.getStringExtra(Action.RESTAURANT_ID_EXTRA);
        Log.d(TAG_LOG, "Ricevo Extra; "+this.id_restaurant);

        this.fragment_reviews_list.setRestaurant_id(id_restaurant);

        this.imageView_back = findViewById(R.id.arrow_back_review_client);
        this.btn_create_review = findViewById(R.id.btn_create_review);
        this.btn_create_review.setVisibility(View.INVISIBLE);


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

        this.btn_create_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Activity_Review.this, Activity_Create_Review.class);
                intent1.putExtra(Action.RESTAURANT_ID_EXTRA, id_restaurant);
                startActivity(intent1);
            }
        });








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

        Log.d(TAG_LOG, "Land? :"+getResources().getBoolean(R.bool.dual_pane));
        if (getResources().getBoolean(R.bool.dual_pane)){
            this.btn_create_review.setVisibility(View.GONE);
            Log.d(TAG_LOG, "rendo il bottone invisibile");
        }
        else {
            this.btn_create_review.setVisibility(View.VISIBLE);
            Log.d(TAG_LOG, "rendo il bottone visibile");
        }
    }


    public void back() {
        Log.d(TAG_LOG, "Back");
        finish();
    }


    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        finish();
    }

    private void check_user(){

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
            Log.d(TAG_LOG, "Non trovato utente con SharedPreference");
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
                                Log.d(TAG_LOG, "Errore - Utente Admin");
                                btn_create_review.setVisibility(View.GONE);

                            }
                            else {
                                Log.d(TAG_LOG, "Utente Visitatore");
                                //btn_create_review.setVisibility(View.VISIBLE);
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