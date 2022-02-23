package com.poma.restaurant.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.model.User;

import java.util.HashMap;
import java.util.Map;

public class Activity_Password extends AppCompatActivity implements Fragment_Password.PasswordInterface {
    private static final String TAG_LOG = Activity_Password.class.getName();

    private static final String OLD_PASS = "com.poma.restaurant.account.password.oldpassword";
    private static final String NEW_PASS = "com.poma.restaurant.account.password.newpassword";
    private static final String REPEAT_PASS = "com.poma.restaurant.account.password.repeatpassword";
    private Fragment_Password fragment;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private Button btn_logout;
    private User user;
    private static String myPassword;
    private ProgressDialog progressDialog;




    //STATE
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(OLD_PASS,this.fragment.get_old());
        outState.putString(NEW_PASS,this.fragment.get_new());
        outState.putString(REPEAT_PASS,this.fragment.get_repeat());

        super.onSaveInstanceState(outState);
        Log.d(TAG_LOG,"Save state");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.fragment.set_old(savedInstanceState.getString(OLD_PASS));
        this.fragment.set_new(savedInstanceState.getString(NEW_PASS));
        this.fragment.set_repeat(savedInstanceState.getString(REPEAT_PASS));

        Log.d(TAG_LOG,"Retrive state");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "On create");

        setContentView(R.layout.activity_password);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "On Start");

        this.fragment = (Fragment_Password)
                getSupportFragmentManager().findFragmentById(R.id.fragment_password_changepassword);

        this.mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = this.mAuth.getCurrentUser();
        if(currentUser == null){
            finish();
        }

        this.db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG_LOG, "l'id del mio utente è " + document.getId());
                        Map<String, Object> data = document.getData();
                        myPassword = (String) data.get("password");
                        Log.d(TAG_LOG, "La mia attuale passwod " + myPassword);

                    } else {
                        Log.d(TAG_LOG, "No such document");
                    }
                } else {
                    Log.d(TAG_LOG, "get failed with ", task.getException());
                }
            }
        });

    }


    @Override
    public void save(String new_password, String old_password) {
        Log.d(TAG_LOG, "save");
        progressBarr(true);

        FirebaseUser current_user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final String email = current_user.getEmail();

        if(!(old_password.equals(myPassword))){
            Log.d(TAG_LOG, "old password wrong");
            this.fragment.setError(getResources().getString(R.string.error_old_pass));
            progressBarr(false);

            return;
        }

        if(new_password.length()<6){
            Log.d(TAG_LOG, "new password weak");
            this.fragment.setError(getResources().getString(R.string.password_weak));
            progressBarr(false);

            return;
        }



        AuthCredential credential = EmailAuthProvider.getCredential(email,old_password);

        current_user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    current_user.updatePassword(new_password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Log.d(TAG_LOG, "new password non salvata");




                            }else {
                                Log.d(TAG_LOG, "new password  salvata");

                                //SALVO NUOVA PASS ANCHE NELL'UTENTE
                                Map<String, Object> data = new HashMap<>();
                                data.put("password",new_password);
                                db.collection("users").document(current_user.getUid()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d(TAG_LOG, "Utente aggiornato");
                                            progressBarr(false);
                                            Toast.makeText(Activity_Password.this, getResources().getString(R.string.password_saved), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Activity_Password.this, Activity_Account.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();

                                        }
                                        else{
                                            Log.d(TAG_LOG, "problemi aggiornamento user");

                                        }
                                    }
                                });


                                //
                            }
                        }
                    });
                }else {
                    Log.d(TAG_LOG, "Auth Fail");

                }
            }
        });
        progressBarr(false);

    }


    private void progressBarr(Boolean b){

        if(b){
            this.progressDialog = new ProgressDialog(Activity_Password.this);
            progressDialog.setMessage(getResources().getString(R.string.login_wait));
            progressDialog.show();
        }
        else{
            this.progressDialog.dismiss();
        }

    }

    @Override
    public void cancel() {
        Log.d(TAG_LOG, "cancel");
        finish();
    }
}