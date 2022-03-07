package com.poma.restaurant.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.poma.restaurant.R;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Register extends AppCompatActivity implements Fragment_Register.RegisterInterface {
    private static final String TAG_LOG = Activity_Register.class.getName();
    private static final String USER_LOGIN_EXTRA = "com.poma.restaurant.USER_LOGIN_EXTRA ";
    private FirebaseFirestore db;
    private DatabaseReference ref;
    private DatabaseReference users;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private User currentUser2;
    private Fragment_Register fragment_register;
    private ProgressDialog progressDialog;
    private Map<String, Object> cities;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.fragment_register = (Fragment_Register)
                getSupportFragmentManager().findFragmentById(R.id.fragment_register);

        Log.d(TAG_LOG, "on create");

    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "on start");
        this.mAuth=FirebaseAuth.getInstance();

        this.fragment_register = (Fragment_Register)
                getSupportFragmentManager().findFragmentById(R.id.fragment_register);

        //controllo la presenza di utenti loggati
        Log.d(TAG_LOG, "Controllo che l'utente non sia già loggato");
        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG_LOG, "Trovato utente con Firestore");
            mAuth.signOut();
        }
        this.currentUser2 = User.load(this);
        if (currentUser2 != null) {
            Log.d(TAG_LOG, "Trovato utente con Shared preferences");
            mAuth.signOut();
            currentUser2.logout(this);
        }
        Log.d(TAG_LOG, "Ora non c'è nessun utente loggato");


        /*
        Log.d(TAG_LOG, "Città 1 - inizio metodo prendere città");
        this.cities = new HashMap<>();
        this.db = FirebaseFirestore.getInstance();
        db.collection("cities").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG_LOG, "Città 2 - On complete");

                if (task.isSuccessful()) {
                    Log.d(TAG_LOG, "Città 3 -  is successfull");

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Map<String, Object> data = document.getData();
                        //Map<String, Object>
                        cities.put(document.getId(), (String)data.get("city"));
                    }
                    fragment_register.setCitiesSpinner(cities, null);
                    Log.d(TAG_LOG, "Città 4 - lista id città :"+cities.toString());

                } else {
                    Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                }
            }
        });
        Log.d(TAG_LOG, "Città 5 - fine");
        */


    }

    @Override
    public void register(String username, String password, String name, String surname, String location, String email, long date) {
        Log.d(TAG_LOG, "register method");
        progressBarr(true);

        //Creazione utente con mail e password
        this.mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Log.d(TAG_LOG, "createUserWithEmail:success");

/*
                            for (Map.Entry<String, Object> entry : cities.entrySet()) {
                                if (entry.getValue().toString().equals(location)) {
                                    city_id = entry.getKey();
                                }
                            }*/

                            //dal nome della città ricava il suo ID
                            getCityIdByName(new Firestore_call_back_cities_Id() {
                                @Override
                                public void onCallback(List<String> list) {
                                    Log.d(TAG_LOG, "On call back per city ID");
                                    String city_id = list.get(0).toString();
                                    Log.d(TAG_LOG, "city id: "+city_id);

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    User user_create;
                                    user_create= User.create(username,password).withDate(date).withEmail(email)
                                            .withLocation(location).withName(name).withSurname(surname).withAdmin(false).withCity_id(city_id).withId(mAuth.getCurrentUser().getUid());

                                    db = FirebaseFirestore.getInstance();

                                    db.collection("users").document(user.getUid()).set(user_create).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                //Toast.makeText(Activity_Register.this, "Creazione account", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG_LOG, "Creo user: admin: "+user_create.getAdmin());
                                                progressBarr(false);
                                                getBack(RESULT_OK, user_create);


                                            }
                                            else{
                                                Toast.makeText(Activity_Register.this, "Problemi creazione account", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG_LOG, "problemi creazione user");
                                                progressBarr(false);


                                            }
                                        }
                                    });




                                }
                            }, "Milano");





                        //FAIL
                        }

                        else {
                            Log.d(TAG_LOG, "createUserWithEmail:fail");

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                Log.d(TAG_LOG, "weak pass: "+password);
                                fragment_register.setError(getResources().getString(R.string.password_weak));
                                progressBarr(false);

                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Log.d(TAG_LOG, "credential exception, error code: "+e.getErrorCode());
                                if (e.getErrorCode() == "ERROR_INVALID_EMAIL"){
                                    fragment_register.setError(getResources().getString(R.string.email_error));
                                    progressBarr(false);
                                }

                            } catch(FirebaseAuthInvalidUserException e) {
                                Log.d(TAG_LOG, "User exception");
                                fragment_register.setError(getResources().getString(R.string.no_user));
                                progressBarr(false);
                            }
                            catch(FirebaseAuthUserCollisionException e) {
                                Log.d(TAG_LOG, "Collision exception");
                                fragment_register.setError(getResources().getString(R.string.user_collision));
                                progressBarr(false);
                            }
                            catch(Exception e) {
                                Log.e(TAG_LOG, e.getMessage());
                                progressBarr(false);
                            }



                        }
                    }
                });

    }

    @Override
    public void update(String username, String name, String surname, String location) {

    }

    private void progressBarr(Boolean b){

        if(b){
            this.progressDialog = new ProgressDialog(Activity_Register.this);
            progressDialog.setMessage(getResources().getString(R.string.register_wait));
            progressDialog.show();
        }
        else{
            this.progressDialog.dismiss();
        }

    }

    private void getBack(int result, User u){
        Intent intent = new Intent();
        //intent.putExtra(User.USER_DATA_EXTRA, u);
        u.save(this);
        Log.d(TAG_LOG, "Salvo shared preferences di utente appena creato: "+u.toString());
        setResult(result,intent);
        finish();
    }

    @Override
    public void cancel() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    @Override
    public void changePassword() {

    }




    //Interfaccia per gestire la query per ricavare id città dato il nome
    public interface Firestore_call_back_cities_Id {
        void onCallback(List<String> list);
    }

    private void getCityIdByName(Firestore_call_back_cities_Id callBack, String name){
        Log.d(TAG_LOG, "getcitybyname");
        List<String> cities_id = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        db.collection("cities")
                .whereEqualTo("city", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG_LOG, "issuccessful");

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG_LOG, "trovato id della città: "+document.getId());
                                cities_id.add(document.getId());
                            }
                            callBack.onCallback(cities_id);
                        } else {
                            Log.w(TAG_LOG, "Error getting documents.", task.getException());
                            cities_id.add("");
                        }

                    }
                });

    }

}

