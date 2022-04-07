package com.poma.restaurant.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;
import com.poma.restaurant.utilities.MyApplication;

import java.util.Map;

import static com.poma.restaurant.utilities.Action.RESULT_OK_ADMIN;

public class Activity_Login extends AppCompatActivity implements Fragment_Login.LoginInterface{
    private static final String TAG_LOG = Activity_Login.class.getName();
    private static final String USER_LOGIN_EXTRA = "com.poma.restaurant.USER_LOGIN_EXTRA ";

    private static FirebaseAuth mAuth;
    private static FirebaseDatabase db;
    private static DatabaseReference ref;
    private static boolean uscita = false;
    private static ProgressDialog progressDialog;
    private static ProgressBar progresBar;
    private static Fragment_Login fragment_login;

    private static FirebaseUser currentUser;
    private static User currentUser2;


    private static Boolean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "on create");

        setContentView(R.layout.activity_login);
        TextView title = (TextView)findViewById(R.id.textview_title_login);
        View view = findViewById(R.id.view_rectangle_login);
        progresBar = (ProgressBar)findViewById(R.id.progress_bar_login);

        //Auth
        mAuth = FirebaseAuth.getInstance();


        //Cambia vista a seconda di login utente o admin
        Intent intent = getIntent(); //receive the intent from Activity_first_access

        if (intent.getBooleanExtra(USER_LOGIN_EXTRA, false)) {
            this.user = true;
            Log.d(TAG_LOG, "Capisco che si tratta di un login Utente");
        }
        else {
            this.user = false;
            Log.d(TAG_LOG, "Capisco che si tratta di un login Admin");
        }

        //se si sta facendo un login per l'amministratore il layout viene cambiato
        if(user==false){
            title.setText(getResources().getString(R.string.login_admin_title));
            view.setBackgroundColor(getResources().getColor(R.color.blue_link));
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "on start");

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
    }



    //Riceve email e password dal Fragment
    //effettua il login considerando email, password e tipo di utente, in caso di problema invoca il metodo setError del Fragment
    @Override
    public void login(String email, String password) {

        Log.d(TAG_LOG, "inizio metodo login, con tipo utente: "+this.user+" (True=user, False=Admin)");
        progressBarr(true);
        fragment_login = (Fragment_Login)
                getSupportFragmentManager().findFragmentById(R.id.fragment_login);

        //LOGIN UTENTE
        if(this.user){ //se ho fatto accesso a questa activity per login utente

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG_LOG, "signInWithEmail:success");
                                fragment_login.setError("");

                                //PRENDO UTENTE
                                Log.d(TAG_LOG, "inizio metodo retrive user");

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Map<String, Object> data = document.getData();
                                                Log.d(TAG_LOG, "DocumentSnapshot data: " + data);
                                                if(!(boolean)data.get("admin")){
                                                    User user = User.create((String) data.get("username"),(String) data.get("password"))
                                                            .withSurname((String) data.get("surname"))
                                                            .withName((String) data.get("name"))
                                                            .withLocation((String) data.get("location"))
                                                            .withEmail((String) data.get("email"))
                                                            .withDate((long) data.get("date"))
                                                            .withId(mAuth.getCurrentUser().getUid());

                                                    Log.d(TAG_LOG, "oggetto User creato con successo");

                                                    //Shared preferences
                                                    user.save(getApplicationContext());
                                                    Log.d(TAG_LOG, "User ID: "+user.getID());
                                                    Intent intent = new Intent();
                                                    //intent.putExtra(Action.RESULT_OK_EXTRA, true);
                                                    setResult(RESULT_OK,intent);
                                                    progressBarr(false);
                                                    finish();
                                                }
                                                else {
                                                    Log.d(TAG_LOG, "questo utente è admin (sto facendo login per utente normale)");
                                                    fragment_login.setError(getResources().getString(R.string.is_admin));
                                                    progressBarr(false);

                                                }


                                            } else {
                                                Log.d(TAG_LOG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG_LOG, "get failed with ", task.getException());
                                        }
                                    }
                                });





                            } else {

                                // If sign in fails, display a message to the user.
                                Log.w(TAG_LOG, "signInWithEmail:failure", task.getException());
                                //Toast.makeText(Activity_Login.this, getResources().getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();

                                try {
                                    throw task.getException();
                                }
                                catch(FirebaseAuthInvalidUserException e) {

                                    Log.d(TAG_LOG, "no user found: "+password);
                                    fragment_login.setError(getResources().getString(R.string.no_user));
                                    progressBarr(false);

                                }
                                catch(FirebaseAuthInvalidCredentialsException e) {
                                    Log.d(TAG_LOG, "credential exception, error code: "+e.getErrorCode());
                                    if (e.getErrorCode() == "ERROR_INVALID_EMAIL"){
                                        fragment_login.setError(getResources().getString(R.string.login_error_email));
                                        progressBarr(false);
                                    }
                                    else if(e.getErrorCode() == "ERROR_WRONG_PASSWORD"){
                                        fragment_login.setError(getResources().getString(R.string.login_error_pass));
                                        progressBarr(false);
                                    }


                                }


                                catch(Exception e) {
                                    Log.e(TAG_LOG, e.getMessage());
                                    progressBarr(false);
                                }


                            }
                            //progressBarr(false);
                        }

                    });
        }
        else { // se ho fatto accesso per login admin
            Log.d(TAG_LOG, "login con utente utente admin");

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                //PRENDO UTENTE
                                Log.d(TAG_LOG, "inizio metodo retrive user");

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Map<String, Object> data = document.getData();
                                                Log.d(TAG_LOG, "DocumentSnapshot data: " + data);

                                                if((boolean)data.get("admin")){

                                                    User user = User.create((String) data.get("username"),(String) data.get("password"))
                                                            .withSurname((String) data.get("surname"))
                                                            .withName((String) data.get("name"))
                                                            .withLocation((String) data.get("location"))
                                                            .withEmail((String) data.get("email"))
                                                            .withDate((long) data.get("date"))
                                                            .withId(mAuth.getCurrentUser().getUid());

                                                    Log.d(TAG_LOG, "oggetto User creato con successo");

                                                    //Shared preferences
                                                    user.save(getApplicationContext());
                                                    Log.d(TAG_LOG, "User ID: "+user.getID());
                                                    Intent intent = new Intent();
                                                    //intent.putExtra(User.USER_DATA_EXTRA, user);
                                                    setResult(RESULT_OK_ADMIN,intent);
                                                    progressBarr(false);
                                                    finish();
                                                }
                                                else{
                                                    Log.d(TAG_LOG, "questo utente non è admin");
                                                    fragment_login.setError(getResources().getString(R.string.no_admin));
                                                    progressBarr(false);
                                                }


                                            } else {
                                                Log.d(TAG_LOG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG_LOG, "get failed with ", task.getException());
                                        }
                                    }
                                });





                            } else {

                                // If sign in fails, display a message to the user.
                                Log.w(TAG_LOG, "signInWithEmail:failure", task.getException());
                                //Toast.makeText(Activity_Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    Log.d(TAG_LOG, "credential exception, error code: "+e.getErrorCode());
                                    if (e.getErrorCode() == "ERROR_INVALID_EMAIL"){
                                        fragment_login.setError(getResources().getString(R.string.login_error_email));
                                        progressBarr(false);
                                    }
                                    else if(e.getErrorCode() == "ERROR_WRONG_PASSWORD"){
                                        fragment_login.setError(getResources().getString(R.string.login_error_pass));
                                        progressBarr(false);
                                    }
                                }
                                catch(FirebaseAuthInvalidUserException e) {
                                    Log.d(TAG_LOG, "no user found: "+password);
                                    fragment_login.setError(getResources().getString(R.string.no_user));
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
        //progressBarr(false);
    }


    @Override
    public void cancel() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    //gestione progress bar
    private void progressBarr(Boolean b){

        if(b){
            this.progressDialog = new ProgressDialog(Activity_Login.this);
            progressDialog.setMessage(getResources().getString(R.string.login_wait));
            progressDialog.show();
        }
        else{
            this.progressDialog.dismiss();
        }

    }

}