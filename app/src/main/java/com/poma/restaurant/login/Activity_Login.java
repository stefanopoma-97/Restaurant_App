package com.poma.restaurant.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.Map;

public class Activity_Login extends AppCompatActivity implements Fragment_Login.LoginInterface{
    private static final String TAG_LOG = Activity_Login.class.getName();
    private static final String USER_LOGIN_EXTRA = "com.poma.restaurant.USER_LOGIN_EXTRA ";
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private boolean uscita = false;
    private ProgressDialog progressDialog;



    private Boolean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG_LOG, "on create");

        TextView title = (TextView)findViewById(R.id.textview_title_login);
        View view = findViewById(R.id.view_rectangle_login);
        Button btn_login = (Button)findViewById(R.id.login_confirm);

        //Auth
        mAuth = FirebaseAuth.getInstance();



        //Cambia vista a seconda di login utente o admin
        Intent intent = getIntent(); //receive the intent from Activity_first_access

        if (intent.getBooleanExtra(USER_LOGIN_EXTRA, false)) this.user = true;
        else this.user = false;

        if(user==false){
            title.setText("Login - Admin");
            title.setTextSize(35);
            view.setBackgroundColor(getResources().getColor(R.color.blue_link));
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "on start");

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //Toast.makeText(Activity_Login.this, "C'è utente: "+currentUser, Toast.LENGTH_SHORT).show();

        }
        else {
            //Toast.makeText(Activity_Login.this, "Non c'è utente: ", Toast.LENGTH_SHORT).show();
            mAuth.signOut();

        }
    }

    @Override
    public void login(String email, String password) {

        Log.d(TAG_LOG, "inizio metodo login, con user: "+this.user);

        Fragment_Login fragment_login = (Fragment_Login)
                getSupportFragmentManager().findFragmentById(R.id.fragment_login);
        final String DUMMY_USERNAME = "stefano";
        final String DUMMY_PASSWORD = "poma";

        //LOGIN UTENTE
        if(this.user){ //se ho fatto accesso a questa activity per login utente

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                progressBarr(true);
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d(TAG_LOG, "signInWithEmail:success, uscita: "+ uscita);


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
                                                User user = User.create((String) data.get("username"),(String) data.get("password"))
                                                            .withSurname((String) data.get("surname"))
                                                            .withName((String) data.get("name"))
                                                            .withLocation((String) data.get("location"))
                                                            .withEmail((String) data.get("email"))
                                                            .withDate((long) data.get("date"));

                                                            Log.d(TAG_LOG, "login con utente fatto");

                                                            Intent intent = new Intent();
                                                            intent.putExtra(User.USER_DATA_EXTRA, user);
                                                            setResult(RESULT_OK,intent);
                                                            progressBarr(false);
                                                            finish();

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
                                Toast.makeText(Activity_Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthInvalidUserException e) {
                                    Log.d(TAG_LOG, "no user found: "+password);
                                    fragment_login.setError(getResources().getString(R.string.no_user));

                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    Log.d(TAG_LOG, "credential exception");
                                    fragment_login.setError(getResources().getString(R.string.login_error_pass));
                                }

                                catch(Exception e) {
                                    Log.e(TAG_LOG, e.getMessage());
                                }


                            }
                        }
                    });
        }
        else { // se ho fatto accesso per login admin
            Log.d(TAG_LOG, "utente admin");
            progressBarr(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();

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
                                                    Toast.makeText(Activity_Login.this, "Login", Toast.LENGTH_SHORT).show();



                                                    User user = User.create((String) data.get("username"),(String) data.get("password"))
                                                            .withSurname((String) data.get("surname"))
                                                            .withName((String) data.get("name"))
                                                            .withLocation((String) data.get("location"))
                                                            .withEmail((String) data.get("email"))
                                                            .withDate((long) data.get("date"));

                                                    Log.d(TAG_LOG, "login con utente(Admin) fatto");

                                                    Intent intent = new Intent();
                                                    intent.putExtra(User.USER_DATA_EXTRA, user);
                                                    setResult(RESULT_OK,intent);
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
                                Toast.makeText(Activity_Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthInvalidUserException e) {
                                    Log.d(TAG_LOG, "no user found: "+password);
                                    fragment_login.setError(getResources().getString(R.string.no_user));

                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    Log.d(TAG_LOG, "credential exception");
                                    fragment_login.setError(getResources().getString(R.string.login_error_pass));
                                }

                                catch(Exception e) {
                                    Log.e(TAG_LOG, e.getMessage());
                                }

                                progressBarr(false);
                            }
                        }
                    });
        }

    }

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

    @Override
    public void cancel() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }
}