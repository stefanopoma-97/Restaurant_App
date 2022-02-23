package com.poma.restaurant.login;

import androidx.annotation.NonNull;
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
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Register extends AppCompatActivity implements Fragment_Register.RegisterInterface {
    private static final String TAG_LOG = Activity_Register.class.getName();
    private static final String USER_LOGIN_EXTRA = "com.poma.restaurant.USER_LOGIN_EXTRA ";
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private DatabaseReference users;
    private FirebaseAuth mAuth;
    private Fragment_Register fragment_register;
    private ProgressDialog progressDialog;
    private Map<String, Object> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(TAG_LOG, "on create");

    }

    @Override
    public void register(String username, String password, String name, String surname, String location, String email, long date) {
        Log.d(TAG_LOG, "register method");

        this.fragment_register = (Fragment_Register)
                getSupportFragmentManager().findFragmentById(R.id.fragment_register);

        this.mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            progressBarr(true);

                            Log.d(TAG_LOG, "createUserWithEmail:success");

                            String city_id="1";

                            for (Map.Entry<String, Object> entry : cities.entrySet()) {
                                if (entry.getValue().toString().equals(location)) {
                                    city_id = entry.getKey();
                                }
                            }
                            Log.d(TAG_LOG, "citta: "+location+", city_id: "+city_id);



                            FirebaseUser user = mAuth.getCurrentUser();
                            User user_create;
                            user_create= User.create(username,password).withDate(date).withEmail(email)
                                    .withLocation(location).withName(name).withSurname(surname).withAdmin(false).withCity_id(city_id);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            db.collection("users").document(user.getUid()).set(user_create).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Activity_Register.this, "Creazione account", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG_LOG, "Creo user: admin: "+user_create.getAdmin());
                                        progressBarr(false);
                                        getBack(RESULT_OK, user_create);


                                    }
                                    else{
                                        Toast.makeText(Activity_Register.this, "Problemi creazione account", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG_LOG, "problemi creazione user");


                                    }
                                }
                            });

                        //FAIL
                        }

                        else {
                            Log.d(TAG_LOG, "createUserWithEmail:fail");

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                Log.d(TAG_LOG, "weak pass: "+password);
                                fragment_register.setError(getResources().getString(R.string.password_weak));

                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Log.d(TAG_LOG, "credential exception");
                                fragment_register.setError(getResources().getString(R.string.email_error));
                            } catch(FirebaseAuthInvalidUserException e) {
                                Log.d(TAG_LOG, "User exception");
                                fragment_register.setError(getResources().getString(R.string.no_user));
                            }
                            catch(FirebaseAuthUserCollisionException e) {
                                Log.d(TAG_LOG, "Collision exception");
                                fragment_register.setError(getResources().getString(R.string.user_collision));
                            }
                            catch(Exception e) {
                                Log.e(TAG_LOG, e.getMessage());
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
        intent.putExtra(User.USER_DATA_EXTRA, u);
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "on start");
        mAuth=FirebaseAuth.getInstance();

        this.fragment_register = (Fragment_Register)
                getSupportFragmentManager().findFragmentById(R.id.fragment_register);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //Toast.makeText(Activity_Register.this, "C'è utente: "+currentUser, Toast.LENGTH_SHORT).show();
            mAuth.signOut();

        }
        else {
            //Toast.makeText(Activity_Register.this, "Non c'è utente: ", Toast.LENGTH_SHORT).show();

        }

        Log.d(TAG_LOG, "1 - inizio metodo prendere città");

        this.cities = new HashMap<>();


        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("cities").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG_LOG, "2 - On complete");

                if (task.isSuccessful()) {
                    Log.d(TAG_LOG, "3 -  is successfull");

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Map<String, Object> data = document.getData();

                        cities.put(document.getId(), (String)data.get("city"));
                    }
                    fragment_register.setCitiesSpinner(cities, null);
                    Log.d(TAG_LOG, "4 - lista id città :"+cities.toString());

                } else {
                    Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                }
            }
        });

        Log.d(TAG_LOG, "5 - inizio metodo prendere città");



    }
}