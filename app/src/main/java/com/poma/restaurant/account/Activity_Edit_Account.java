package com.poma.restaurant.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.poma.restaurant.R;
import com.poma.restaurant.login.Activity_Password;
import com.poma.restaurant.login.Activity_Register;
import com.poma.restaurant.login.Fragment_Register;
import com.poma.restaurant.model.User;

import java.util.HashMap;
import java.util.Map;

public class Activity_Edit_Account extends AppCompatActivity implements Fragment_Register.RegisterInterface {
    private static final String TAG_LOG = Activity_Edit_Account.class.getName();
    private static final String USERNAME = "com.poma.restaurant.account.username";
    private static final String PASS = "com.poma.restaurant.account.pass";
    private static final String NAME = "com.poma.restaurant.account.name";
    private static final String SURNAME = "com.poma.restaurant.account.surname";
    private static final String EMAIL = "com.poma.restaurant.account.email";
    private static final String LOCATION = "com.poma.restaurant.account.location";
    private static final String DATE = "com.poma.restaurant.account.date";
    private static final String CITY = "com.poma.restaurant.account.city";

    private FirebaseFirestore db;
    private DatabaseReference ref;
    private DatabaseReference users;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private User currentUser2;


    private Button btn_logout;
    private User user_new;
    private Map<String, Object> cities;


    private Fragment_Register fragment;

    private ProgressDialog progressDialog;

    /*
    //TODO la città e le altre info viene sovrascritta dalle informazione di onstart
    //STATE
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(USERNAME,this.fragment.getE_username());
        outState.putString(NAME,this.fragment.getE_name());
        outState.putString(SURNAME,this.fragment.getE_surname());
        outState.putString(PASS,this.fragment.getE_password());
        outState.putString(EMAIL,this.fragment.getE_email());
        outState.putString(LOCATION,this.fragment.getE_location());
        outState.putString(CITY,this.fragment.getCity());
        outState.putLong(DATE,this.fragment.getE_date());

        super.onSaveInstanceState(outState);
        Log.d(TAG_LOG,"Save state");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.fragment.setE_username(savedInstanceState.getString(USERNAME));
        this.fragment.setE_name(savedInstanceState.getString(NAME));
        this.fragment.setE_surname(savedInstanceState.getString(SURNAME));
        this.fragment.setE_password(savedInstanceState.getString(PASS));
        this.fragment.setE_email(savedInstanceState.getString(EMAIL));
        this.fragment.setE_location(savedInstanceState.getString(LOCATION));
        this.fragment.setCitySpinnerValue(savedInstanceState.getString(CITY));
        this.fragment.setE_date(savedInstanceState.getLong(DATE));

        Log.d(TAG_LOG,"Retrive state");

    }
    */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "On create");
        setContentView(R.layout.activity_edit_account);
        this.fragment = (Fragment_Register)
                getSupportFragmentManager().findFragmentById(R.id.fragment_account_register);

    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "On Start");

        this.mAuth=FirebaseAuth.getInstance();

        //Controllo utenti
        check_user();

        //imposta il fragment in modalità modifica
        set_fragment_modifica();



        this.currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //Toast.makeText(Activity_Account.this, "C'è utente: "+currentUser, Toast.LENGTH_SHORT).show();
            //PRENDO UTENTE
            Log.d(TAG_LOG, "inizio metodo retrive user");

            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
                            Log.d(TAG_LOG, "DocumentSnapshot data: " + data);


                            user_new = User.create((String) data.get("username"),(String) data.get("password"))
                                    .withSurname((String) data.get("surname"))
                                    .withName((String) data.get("name"))
                                    .withLocation((String) data.get("location"))
                                    .withEmail((String) data.get("email"))
                                    .withDate((long) data.get("date"))
                                    .withAdmin((Boolean) data.get("admin"))
                                    .withCity_id((String) data.get("city_id"))
                                    .withId(mAuth.getCurrentUser().getUid());

                            fragment.setE_date((long) data.get("date"));
                            //fragment.setT_email((String) data.get("email"));
                            //fragment.setE_password((String) data.get("password"));
                            fragment.setE_location((String) data.get("location"));
                            fragment.setE_name((String) data.get("name"));
                            fragment.setE_surname((String) data.get("surname"));
                            fragment.setE_username((String) data.get("username"));

                            cities = new HashMap<>();
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
                                        fragment.setCitiesSpinner(cities, user_new.getLocation());
                                        Log.d(TAG_LOG, "4 - lista id città :"+cities.toString());

                                    } else {
                                        Log.d(TAG_LOG, "Error getting documents: ", task.getException());
                                    }
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
        else {
            Log.d(TAG_LOG, "No such document");
            finish();
            //Toast.makeText(Activity_Account.this, "Non c'è utente: ", Toast.LENGTH_SHORT).show();
            fragment.setE_date(1521815495631L);
            fragment.setT_email("email settata");
            fragment.setE_password("password settata");
            fragment.setE_location("location settata");
            fragment.setE_name("name settato");
            fragment.setE_surname("surname set");
            fragment.setE_username("username");
        }



    }

    //TODO molto invasivo, forse meglio per altre funzioni
    public void popupMessage(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.update_account_info));
        alertDialogBuilder.setTitle(getResources().getString(R.string.update_account));

        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void register(String username, String password, String name, String surname, String location, String email, long date) {
        return;
    }

    @Override
    public void update(String username, String name, String surname, String location) {
        Log.d(TAG_LOG, "metodo update, attualmente utente loggato: "+this.user_new.getEmail());
        progressBarr(true);
        FirebaseUser firebase_user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //creo hash map con cui aggiornare utente
        Map<String, Object> data = new HashMap<>();
        if(!(this.user_new.getUsername().equals(username)))
            data.put("username", username);
        if(!(this.user_new.getName().equals(name)))
            data.put("name", name);
        if(!(this.user_new.getSurname().equals(surname)))
            data.put("surname", surname);
        if(!(this.user_new.getLocation().equals(location))){
            data.put("location", location);
            String city_id=this.user_new.getCity_id();

            for (Map.Entry<String, Object> entry : cities.entrySet()) {
                if (entry.getValue().toString().equals(location)) {
                    city_id = entry.getKey();
                    data.put("city_id", city_id);
                }
            }


            Log.d(TAG_LOG, "citta: "+location+", city_id: "+city_id);
            Log.d(TAG_LOG, "data: "+data.toString());
        }


        db.collection("users").document(firebase_user.getUid()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG_LOG, "Utente aggiornato");
                    progressBarr(false);
                    getBack(RESULT_OK);
                    //popupMessage();
                }
                else{
                    Log.d(TAG_LOG, "problemi aggiornamento user");
                    progressBarr(false);

                }
            }
        });
    }

    @Override
    public void cancel() {
        getBack(RESULT_CANCELED);
    }

    @Override
    public void changePassword() {

    }

    private void getBack(int result){
        Intent intent = new Intent();
        setResult(result,intent);
        finish();
    }

    private void progressBarr(Boolean b){

        if(b){
            this.progressDialog = new ProgressDialog(Activity_Edit_Account.this);
            progressDialog.setMessage(getResources().getString(R.string.register_wait));
            progressDialog.show();
        }
        else{
            this.progressDialog.dismiss();
        }

    }


    //controllo la presenza di un utente loggato.
    //per farlo viene utilizzato Firestore e SharedPreferences. Viene anche confrontato l'id degli utenti ricavato nei due modi
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
        else if (currentUser.getUid() == currentUser2.getID()){
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

    //imposta il layout del fragment nella modalità di "Modifica"
    private void set_fragment_modifica() {
        //Imposta il Fragment in modalità modifica
        this.fragment.setModifica(true);
        this.fragment.setVisibilityDate(View.GONE);
        //this.fragment.setVisibilityBtnPassword(View.VISIBLE);
        //fragment.setVisibilityTextViewEmail(View.VISIBLE);
        this.fragment.setVisibilityPassword(View.INVISIBLE);
        this.fragment.setVisibilityEmail(View.INVISIBLE);

        this.fragment.setRegisterText(getResources().getString(R.string.update));
        this.fragment.setCancelText(getResources().getString(R.string.back));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
    }

}