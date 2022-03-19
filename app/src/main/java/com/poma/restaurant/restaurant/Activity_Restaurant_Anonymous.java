package com.poma.restaurant.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

public class Activity_Restaurant_Anonymous extends AppCompatActivity implements Fragment_Restaurant_Client.RestaurantInterface {

    private static final String TAG_LOG = Activity_Restaurant_Anonymous.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;

    private BroadcastReceiver broadcastReceiver;

    private static Fragment_Restaurant_Client fragment_restaurant_client;
    private ImageView imageView_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_anonymous);

        this.fragment_restaurant_client = (Fragment_Restaurant_Client)
                getSupportFragmentManager().findFragmentById(R.id.fragment_restaurant_anonymous);
        this.mAuth= FirebaseAuth.getInstance();
        Intent intent = getIntent(); //receive the intent
        String id_restaurant= intent.getStringExtra(Action.RESTAURANT_ID_EXTRA);



        Log.d(TAG_LOG, "Ricavo il seguente ID del ristorante: "+id_restaurant);
        this.fragment_restaurant_client.setAnonymous();
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


        this.imageView_back = (ImageView)findViewById(R.id.arrow_back_restaurant_anonymous);
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
        check_user_anonymous();
    }

    public void back() {
        Log.d(TAG_LOG, "Back");
        finish();
    }


    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        finish();
    }

    private void check_user_anonymous(){

        Log.d(TAG_LOG, "Controllo ci sia un utente loggato");
        //Login Firestore
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "Trovato utente con Firestore");
            this.mAuth.signOut();
            finish();
        }

        //Login Shared Preferences
        this.currentUser2 = User.load(this);
        if (currentUser2 != null) {
            Log.d(TAG_LOG, "Trovato utente con Firestore");
            this.currentUser2.logout(this);
            finish();
        }


    }

    @Override
    public void goBack() {
        finish();
    }

    @Override
    public void edit_restaurant(String restaurant_id) {
    }

}