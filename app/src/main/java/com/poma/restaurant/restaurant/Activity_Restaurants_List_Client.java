package com.poma.restaurant.restaurant;

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
import com.poma.restaurant.databinding.ActivityMenuBinding;
import com.poma.restaurant.databinding.ActivityRestaurantsListClientBinding;
import com.poma.restaurant.login.Activity_First_Access;
import com.poma.restaurant.menu.Activity_Drawer_Menu_User;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.RecyclerViewAdapter.OnRestaurantClickListener;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.model.User;
import com.poma.restaurant.notifications.Activity_Notification;
import com.poma.restaurant.notifications.Activity_Notifications;
import com.poma.restaurant.notifications.Fragment_Notification_List;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.Map;

public class Activity_Restaurants_List_Client extends Activity_Drawer_Menu_User implements Fragment_Restaurants_List_Client.RestaurantListInterfaceClient, OnRestaurantClickListener {
    ActivityRestaurantsListClientBinding activityRestaurantsListClientBinding;
    private static final String RESTAURANT_ID_EXTRA = "com.poma.restaurant.RESTAURANT_ID_EXTRA";

    private static final String TAG_LOG = Activity_Restaurants_List_Client.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;

    private BroadcastReceiver broadcastReceiver;

    private static Fragment_Restaurants_List_Client fragment_restaurants_list_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "On Create");
        //setContentView(R.layout.activity_restaurants_list_client);

        //Menu laterale
        activityRestaurantsListClientBinding = ActivityRestaurantsListClientBinding.inflate(getLayoutInflater());
        setContentView(activityRestaurantsListClientBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.restaurant));

        this.mAuth= FirebaseAuth.getInstance();
        this.fragment_restaurants_list_client = (Fragment_Restaurants_List_Client)
                getSupportFragmentManager().findFragmentById(R.id.fragment_restaurants_list_client);
        //this.fragment_restaurants_list_client.setAdmin(true);


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
        Log.d(TAG_LOG, "On Start");
        check_user();
    }




    private void logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        finish();
    }

    private void check_user(){

        Log.d(TAG_LOG, "Check users");

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


    //click sul ristorante
    @Override
    public void onRestaurantClick(Restaurant n) {
        final Intent intent = new Intent(Activity_Restaurants_List_Client.this, Activity_Restaurant_Client.class);
        intent.putExtra(RESTAURANT_ID_EXTRA, n.getId());
        startActivity(intent);

    }

    //goBack ricevuto dal fragment
    @Override
    public void goBack() {
        finish();
    }
}