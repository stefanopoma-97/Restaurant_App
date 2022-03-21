package com.poma.restaurant.restaurant;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.databinding.ActivityFavouriteBinding;
import com.poma.restaurant.menu.Activity_Drawer_Menu_User;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Favourite;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.RecyclerViewAdapter.OnFavouriteClickListener;
import com.poma.restaurant.model.User;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.Map;

public class Activity_Favorites extends Activity_Drawer_Menu_User implements Fragment_Favourites.FavouriteListInterfaceClient, OnFavouriteClickListener {
    ActivityFavouriteBinding activityFavouriteBinding;
    private static final String RESTAURANT_ID_EXTRA = "com.poma.restaurant.RESTAURANT_ID_EXTRA";

    private static final String TAG_LOG = Activity_Restaurants_List_Client.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;

    private BroadcastReceiver broadcastReceiver;

    private static Fragment_Favourites fragment_restaurants_list_favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_restaurants_list_client);

        //Menu laterale
        activityFavouriteBinding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(activityFavouriteBinding.getRoot());
        allocateActivityTitle("Favourite");

        this.mAuth= FirebaseAuth.getInstance();
        this.fragment_restaurants_list_favourite = (Fragment_Favourites)
                getSupportFragmentManager().findFragmentById(R.id.fragment_restaurants_list_favourite);


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
        check_user();
    }




    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        finish();
    }

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
                                Log.d(TAG_LOG, "L'utente è admin");
                                finish();
                            }
                            else {
                                Log.d(TAG_LOG, "L'utente è visitatore");
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




    //goBack ricevuto dal fragment
    @Override
    public void goBack() {

        finish();
    }

    @Override
    public void onRestaurantClick(Favourite n) {
        final Intent intent = new Intent(Activity_Favorites.this, Activity_Favorite_Restaurant.class);
        intent.putExtra(RESTAURANT_ID_EXTRA, n.getRestaurant_id());
        startActivity(intent);
    }
}