package com.poma.restaurant.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Broadcast_receiver_callBack_logout;
import com.poma.restaurant.model.Receiver;
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
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private User user_new;

    private Button btn_logout;

    private User user;
    private static String myPassword;
    private ProgressDialog progressDialog;

    private BroadcastReceiver broadcastReceiver;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "On create");

        setContentView(R.layout.activity_password);

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
        this.mAuth=FirebaseAuth.getInstance();

        check_user();

        this.fragment = (Fragment_Password)
                getSupportFragmentManager().findFragmentById(R.id.fragment_password_changepassword);



    }


    @Override
    protected void onDestroy() {
        Log.d(TAG_LOG,"on destroy");
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
        Log.d(TAG_LOG,"un register receiver");
    }


    @Override
    public void save(String new_password, String old_password) {
        Log.d(TAG_LOG, "save");
        progressBarr(true);

        FirebaseUser current_user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final String email = current_user.getEmail();

        /*
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
        }*/



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
                                            //Toast.makeText(Activity_Password.this, getResources().getString(R.string.password_saved), Toast.LENGTH_SHORT).show();
                                            getBack(RESULT_OK);

                                        }
                                        else{
                                            Log.d(TAG_LOG, "problemi aggiornamento user");
                                            progressBarr(false);

                                        }
                                    }
                                });


                                //
                            }
                        }
                    });
                }else {
                    Log.d(TAG_LOG, "Auth Fail");
                    // If sign in fails, display a message to the user.
                    //Toast.makeText(Activity_Login.this, getResources().getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();

                    try {
                        throw task.getException();
                    }

                    catch(FirebaseAuthInvalidCredentialsException e) {
                        Log.d(TAG_LOG, "credential exception, error code: "+e.getErrorCode());
                        if(e.getErrorCode() == "ERROR_WRONG_PASSWORD"){
                            fragment.setError(getResources().getString(R.string.login_error_pass));
                            progressBarr(false);
                        }


                    }


                    catch(Exception e) {
                        Log.e(TAG_LOG, e.getMessage());
                        progressBarr(false);
                    }
                    progressBarr(false);

                }
            }
        });


    }


    private void progressBarr(Boolean b){

        if(b){
            this.progressDialog = new ProgressDialog(Activity_Password.this);
            progressDialog.setMessage(getResources().getString(R.string.saving_wait));
            progressDialog.show();
        }
        else{
            this.progressDialog.dismiss();
        }

    }

    private void getBack(int result){
        Intent intent = new Intent();
        setResult(result,intent);
        finish();
    }

    @Override
    public void cancel() {
        Log.d(TAG_LOG, "cancel");
        finish();
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
            Log.d(TAG_LOG, "ERRORE - ho trovato sulo un utente");
            finish();
        }
        else if (currentUser.getUid().equals(currentUser2.getID())){
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
}