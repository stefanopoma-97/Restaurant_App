package com.poma.restaurant.filter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import com.poma.restaurant.notifications.Activity_Notification;
import com.poma.restaurant.notifications.Fragment_Notification;
import com.poma.restaurant.utilities.Action;

import java.util.ArrayList;
import java.util.Map;

public class Activity_Filter extends AppCompatActivity implements Fragment_Filter.FilterInterface {

    private static final String SEARCH_KEY_FRAGMENT_RESTAURANTS_LIST = "com.poma.restaurant.SEARCH_KEY_FRAGMENT_RESTAURANTS_LIST";
    private static final String TAG_LOG = Activity_Filter.class.getName();
    private ImageView back_button;

    //Filter
    private String city_filter = "";
    private Float vote_filter = new Float(0);
    private ArrayList<String> categories_filter = new ArrayList<>();

    private static Fragment_Filter fragment_filter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        this.fragment_filter = (Fragment_Filter)
                getSupportFragmentManager().findFragmentById(R.id.fragment_filter);

        this.mAuth= FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();

        this.back_button = findViewById(R.id.arrow_back_filter);




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


        this.back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
                finish();
            }
        });




        Intent intent = getIntent(); //receive the intent from Activity_first_access
        this.city_filter = intent.getStringExtra(Action.FILTER_CITY_EXTRA);
        this.vote_filter = intent.getFloatExtra(Action.FILTER_VOTE_EXTRA, new Float(0));
        this.categories_filter = intent.getStringArrayListExtra(Action.FILTER_CATEGORY_EXTRA);



        this.fragment_filter.setCity_filter(city_filter);
        this.fragment_filter.setCategories_filter(categories_filter);
        this.fragment_filter.setVote_filter(vote_filter);
        this.fragment_filter.setFiltered(is_past_filter());

    }

    private Boolean is_past_filter(){
        boolean filtered = false;
        if (!this.city_filter.equals(""))
            filtered = true;
        if(this.vote_filter > 0)
            filtered=true;
        if(this.categories_filter.size()>0)
            filtered=true;
        Log.d(TAG_LOG, "Is past filtered: "+filtered);
        return filtered;
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

    @Override
    public void reset_filter(){
        Intent intent = new Intent();
        intent.putExtra(Action.FILTER_CITY_EXTRA, "");
        intent.putExtra(Action.FILTER_VOTE_EXTRA, new Float(0));
        intent.putExtra(Action.FILTER_CATEGORY_EXTRA, new ArrayList<String>());
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void filter(String city, Float vote, ArrayList<String> categories) {
        this.categories_filter=categories;
        this.city_filter=city;
        this.vote_filter=vote;
        Intent intent = new Intent();
        intent.putExtra(Action.FILTER_CITY_EXTRA, this.city_filter);
        intent.putExtra(Action.FILTER_VOTE_EXTRA, this.vote_filter);
        intent.putExtra(Action.FILTER_CATEGORY_EXTRA, this.categories_filter);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void back(){
        Intent intent = new Intent();
        intent.putExtra(Action.FILTER_CITY_EXTRA, this.city_filter);
        intent.putExtra(Action.FILTER_VOTE_EXTRA, this.vote_filter);
        intent.putExtra(Action.FILTER_CATEGORY_EXTRA, this.categories_filter);
        setResult(RESULT_OK,intent);
        finish();
    }


}