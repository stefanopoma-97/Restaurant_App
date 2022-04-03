package com.poma.restaurant.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
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
import com.poma.restaurant.databinding.ActivityCreateRestaurantBinding;
import com.poma.restaurant.databinding.ActivityEditAccountBinding;
import com.poma.restaurant.databinding.ActivityEditRestaurantBinding;
import com.poma.restaurant.login.Fragment_Register;
import com.poma.restaurant.menu.Activity_Drawer_Menu_Admin;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Receiver;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Edit_Restaurant extends Activity_Drawer_Menu_Admin implements Fragment_Edit_Restaurant.CreateRestaurantInterface {

    ActivityEditRestaurantBinding activityEditRestaurantBinding;

    private static final String TAG_LOG = Activity_Create_Restaurant.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;


    private BroadcastReceiver broadcastReceiver;

    Fragment_Edit_Restaurant fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Menu laterale
        activityEditRestaurantBinding = ActivityEditRestaurantBinding.inflate(getLayoutInflater());
        setContentView(activityEditRestaurantBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.edit_restaurant));

        this.mAuth= FirebaseAuth.getInstance();

        this.fragment = (Fragment_Edit_Restaurant)
                getSupportFragmentManager().findFragmentById(R.id.fragment_edit_restaurant_edit);

        //Imposta il fragment per la modifica
        Intent intent = getIntent(); //receive the intent from Activity_first_access
        String id_restaurant= intent.getStringExtra(Action.RESTAURANT_ID_EXTRA);
        this.fragment.setRestaurantID(id_restaurant);
        this.fragment.setUpdate();

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
        Float n2 = new Float(0);
        r.setN_reviews(n);
        r.setVote(n2);

        db = FirebaseFirestore.getInstance();
        db.collection("restaurants").add(r).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG_LOG, "aggiunta ristorante");
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

        db.collection("users").whereEqualTo("admin", false).whereEqualTo("location", city).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
    public void update_restaurant(String name, String description, String email, String address, String city, String phone, String admin_id, String imageUrl, String category, List<String> tags, String id) {
        Log.d(TAG_LOG, "Create restuaruant");

        Map<String, Object> data = new HashMap<>();

        data.put("name", name);
        data.put("description", description);
        data.put("address", address);
        data.put("city", city);
        data.put("phone", phone);
        data.put("category", category);
        data.put("tags", tags);


        DocumentReference document = db.collection("restaurants").document(id);
        document.set(data, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG_LOG, "aggiorno ristorante");
                            update_favourites(id, name, category, address);
                            Intent intent = new Intent();
                            setResult(RESULT_OK,intent);
                            Toast.makeText(Activity_Edit_Restaurant.this, R.string.restaurant_data_saved, Toast.LENGTH_SHORT).show();
                            finish();

                        }
                        else{
                            Log.d(TAG_LOG, "problemi aggiornamento notifica");

                        }
                    }
                });

    }


    private void update_favourites(String restaurant_id, String name, String category, String address){
        Log.d(TAG_LOG, "Update reviews");
        db.collection("favourites").whereEqualTo("restaurant_id", restaurant_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG_LOG, "On complete");

                if (task.isSuccessful()) {
                    Log.d(TAG_LOG, "is successfull");

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Map<String, Object> data = document.getData();
                        Log.d(TAG_LOG, "Favourite: "+data.toString());
                        update_single_favourite(document.getId(), name, category, address);
                    }

                } else {
                    Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void update_single_favourite(String favourite_id, String name, String category, String address){
        Log.d(TAG_LOG, "aggionro singola review");

        Map<String, Object> data = new HashMap<>();

        data.put("restaurant_name", name);
        data.put("restaurant_category", category);
        data.put("restaurant_address", address);


        DocumentReference document = db.collection("favourites").document(favourite_id);
        document.set(data, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG_LOG, "aggiorno preferito");

                        }
                        else{
                            Log.d(TAG_LOG, "problemi aggiornamento preferito");

                        }
                    }
                });
    }


    @Override
    public void cancel() {
        finish();
    }
}