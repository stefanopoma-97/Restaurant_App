package com.poma.restaurant.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.databinding.ActivityRestarantsListAnonymousBinding;
import com.poma.restaurant.databinding.ActivityRestaurantsListClientBinding;
import com.poma.restaurant.menu.Activity_Drawer_Menu_Anonymous;
import com.poma.restaurant.menu.Activity_Drawer_Menu_User;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.RecyclerViewAdapter.OnRestaurantClickListener;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

public class Activity_Restaurants_List_Anonymous extends Activity_Drawer_Menu_Anonymous implements Fragment_Restaurants_List_Client.RestaurantListInterfaceClient, OnRestaurantClickListener {

    ActivityRestarantsListAnonymousBinding activityRestarantsListAnonymousBinding;

    private static final String TAG_LOG = Activity_Restaurants_List_Anonymous.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;

    private BroadcastReceiver broadcastReceiver;

    private static Fragment_Restaurants_List_Client fragment_restaurants_list_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_restarants_list_anonymous);

        //Menu laterale
        activityRestarantsListAnonymousBinding = ActivityRestarantsListAnonymousBinding.inflate(getLayoutInflater());
        setContentView(activityRestarantsListAnonymousBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.restaurant)+" Anonymous");

        this.mAuth= FirebaseAuth.getInstance();
        this.fragment_restaurants_list_client = (Fragment_Restaurants_List_Client)
                getSupportFragmentManager().findFragmentById(R.id.fragment_restaurants_list_anonymous);
        this.fragment_restaurants_list_client.setAnonymous(true);

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
        check_user_anonymous();
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

    //click sul ristorante
    @Override
    public void onRestaurantClick(Restaurant n) {
        final Intent intent = new Intent(Activity_Restaurants_List_Anonymous.this, Activity_Restaurant_Anonymous.class);
        intent.putExtra(Action.RESTAURANT_ID_EXTRA, n.getId());
        startActivity(intent);

    }

    //goBack ricevuto dal fragment
    @Override
    public void goBack() {
        finish();
    }
}