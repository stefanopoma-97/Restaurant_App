package com.poma.restaurant.review;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.poma.restaurant.R;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.Review;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Create_Review extends AppCompatActivity implements Fragment_Create_Review.CreateReviewInterface {
    private static final String TAG_LOG = Activity_Create_Review.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private String id_restaurant;
    private String username;

    private BroadcastReceiver broadcastReceiver;

    private static Fragment_Create_Review fragment_create_review;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);

        this.mAuth= FirebaseAuth.getInstance();
        this.fragment_create_review = (Fragment_Create_Review)
                getSupportFragmentManager().findFragmentById(R.id.fragment_create_review);

        Intent intent = getIntent(); //receive the intent
        this.id_restaurant= intent.getStringExtra(Action.RESTAURANT_ID_EXTRA);

        this.fragment_create_review.setRestaurant_id(this.id_restaurant);


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
                                finish();

                            }
                            else {
                                Log.d(TAG_LOG, "Utente Visitatore");
                                username = (String)data.get("username");

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
    public void cancel() {
        finish();
    }

    @Override
    public void create_review(String experience, String service, String location, String problems, Float vote, String restaurant_id) {
        Log.d(TAG_LOG, "Creazione review");


        Review n = new Review();
        n.setUser_id(mAuth.getCurrentUser().getUid());
        n.setRestaurant_id(restaurant_id);
        n.setExperience(experience);
        n.setLocation(location);
        n.setService(service);
        n.setProblems(problems);
        n.setUsername(this.username);
        n.setVote(vote);

        Log.d(TAG_LOG, "Creata la seguente Review: "+n.toString());


        db = FirebaseFirestore.getInstance();
        db.collection("reviews").add(n).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG_LOG, "aggiunta recensione");
                search_restaurant_vote(id_restaurant);
                create_notification_new_review(restaurant_id);
                Toast.makeText(Activity_Create_Review.this, R.string.create_review, Toast.LENGTH_SHORT).show();
                back();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG_LOG, "Error adding preferito", e);
                    }
                });
    }

    private void create_notification_new_review(String r_id){
        Log.d(TAG_LOG, "Creazione notifica");

        DocumentReference docRef = this.db.collection("restaurants").document(r_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d(TAG_LOG, "DocumentSnapshot data: " + data);

                        String admin_id = (String) data.get("admin_id");

                        Notification n = new Notification();
                        n.setUser_id(admin_id);
                        n.setRead(false);
                        n.setShowed(false);
                        n.setContent(getResources().getString(R.string.new_review_description)+" "+(String) data.get("name"));
                        n.setType(getResources().getString(R.string.new_review));
                        n.setUseful_id(document.getId());
                        Calendar calendar = Calendar.getInstance();
                        long timeMilli2 = calendar.getTimeInMillis();
                        n.setDate(timeMilli2);
                        Log.d(TAG_LOG, "notifica istanziata: "+n.toString());



                        db = FirebaseFirestore.getInstance();
                        db.collection("notifications").add(n).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG_LOG, "aggiunta notifica recensione");
                            }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG_LOG, "Error adding notifica", e);
                                }
                            });


                    } else {
                        Log.d(TAG_LOG, "No such document");
                    }
                } else {
                    Log.d(TAG_LOG, "get failed with ", task.getException());
                }
            }
        });


    }



    private void search_restaurant_vote(String restaurant_id){
        ArrayList<Float> votes = new ArrayList<>();
        Log.d(TAG_LOG, "Update resturant vote");
        db.collection("reviews").whereEqualTo("restaurant_id", restaurant_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG_LOG, "On complete");

                if (task.isSuccessful()) {
                    Log.d(TAG_LOG, "is successfull");

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Map<String, Object> data = document.getData();
                        Log.d(TAG_LOG, "reviews: "+data.toString());
                        Double d = (Double) data.get("vote");
                        votes.add(new Float(d.floatValue()));
                    }

                    update_restaurant_vote(votes, id_restaurant);

                } else {
                    Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void update_restaurant_vote(ArrayList<Float> votes, String restaurant_id){
        //////
        Map<String, Object> data = new HashMap<>();

        Float average = new Float(0);
        for (Float f : votes) {
            average += f;
        }
        average = average / votes.size();
        data.put("vote", average);
        data.put("n_reviews", votes.size());

        Log.d(TAG_LOG, "MEDIA: "+average+" SIZE: "+votes.size());


        DocumentReference document = db.collection("restaurants").document(restaurant_id);
        document.set(data, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG_LOG, "aggiorno ristorante");

                        }
                        else{
                            Log.d(TAG_LOG, "problemi aggiornamento ristorante");

                        }
                    }
                });
    }


}