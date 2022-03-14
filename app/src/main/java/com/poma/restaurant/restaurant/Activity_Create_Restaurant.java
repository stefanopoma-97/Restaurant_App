package com.poma.restaurant.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

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
import com.poma.restaurant.R;
import com.poma.restaurant.databinding.ActivityCreateRestaurantBinding;
import com.poma.restaurant.databinding.ActivityRestaurantsListClientBinding;
import com.poma.restaurant.menu.Activity_Drawer_Menu_Admin;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Activity_Create_Restaurant extends Activity_Drawer_Menu_Admin implements Fragment_Edit_Restaurant.CreateRestaurantInterface {

    ActivityCreateRestaurantBinding activityCreateRestaurantBinding;

    private static final String TAG_LOG = Activity_Create_Restaurant.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;


    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Menu laterale
        activityCreateRestaurantBinding = ActivityCreateRestaurantBinding.inflate(getLayoutInflater());
        setContentView(activityCreateRestaurantBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.add_new_restaurant));

        this.mAuth= FirebaseAuth.getInstance();

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
    public void create_restaurant(String name, String description, String email, String address, String city, String phone, String admin_id, String imageUrl, String category, List<String> tags) {
        Log.d(TAG_LOG, "Create restuaruant");
        Restaurant r = new Restaurant();

        r.setName(name);
        r.setDescription(description);
        r.setEmail(email);
        r.setAddress(address);
        r.setCity(city);
        r.setPhone(phone);
        r.setCategory(category);
        r.setImageUrl(imageUrl);
        r.setAdmin_id(admin_id);
        r.setTags(tags);

        Integer n = new Integer(0);
        Integer n2 = new Integer(0);
        r.setN_reviews(n);
        r.setVote(n2);

        db = FirebaseFirestore.getInstance();
        db.collection("restaurants").add(r).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG_LOG, "aggiunta ristorante");
                //TODO creare notifica
                nuova_notifica_ristorante(city, documentReference.getId());
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG_LOG, "Error adding ristorante", e);
                    }
                });


    }

    private void nuova_notifica_ristorante(String city, String restaurant_id){
        Log.d(TAG_LOG, "Creazione notifica ristorante");

        db.collection("users").whereEqualTo("admin", false).whereEqualTo("city", city).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    Log.d(TAG_LOG, "is successfull");

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Notification n = new Notification();
                        n.setUser_id(document.getId());
                        n.setRead(false);
                        n.setShowed(false);
                        n.setContent(getResources().getString(R.string.new_restaurant_content));
                        n.setType(getResources().getString(R.string.new_restaurant));
                        n.setUseful_id(restaurant_id);
                        Calendar calendar = Calendar.getInstance();
                        long timeMilli2 = calendar.getTimeInMillis();
                        n.setDate(timeMilli2);
                        Log.d(TAG_LOG, "notifica istanziata: "+n.getType());



                        db = FirebaseFirestore.getInstance();
                        db.collection("notifications").add(n).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG_LOG, "aggiunta notifica ristorante");
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG_LOG, "Error adding notifica", e);
                                    }
                                });
                    }




                } else {
                    Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                }
            }
        });



    }

    @Override
    public void update_restaurant(String name, String description, String email, String address, String city, String phone, String admin_id, String imageUrl, String category, List<String> tags) {

    }

    @Override
    public void cancel() {
        finish();
    }
}