package com.poma.restaurant.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.poma.restaurant.R;
import com.poma.restaurant.model.User;

public class Activity_Account extends AppCompatActivity implements Fragment_Register.RegisterInterface {
    private static final String TAG_LOG = Activity_Account.class.getName();
    private static final String USERNAME = "com.poma.restaurant.account.username";
    private static final String PASS = "com.poma.restaurant.account.pass";
    private static final String NAME = "com.poma.restaurant.account.name";
    private static final String SURNAME = "com.poma.restaurant.account.surname";
    private static final String EMAIL = "com.poma.restaurant.account.email";
    private static final String LOCATION = "com.poma.restaurant.account.location";
    private static final String DATE = "com.poma.restaurant.account.date";


    private Fragment_Register fragment;


    //STATE
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(USERNAME,this.fragment.getE_username());
        outState.putString(NAME,this.fragment.getE_name());
        outState.putString(SURNAME,this.fragment.getE_surname());
        outState.putString(PASS,this.fragment.getE_password());
        outState.putString(EMAIL,this.fragment.getE_email());
        outState.putString(LOCATION,this.fragment.getE_location());
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
        this.fragment.setE_date(savedInstanceState.getLong(DATE));

        Log.d(TAG_LOG,"Retrive state");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "On create");
        setContentView(R.layout.activity_account);





    }

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
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "On Start");
        this.fragment = (Fragment_Register)
                getSupportFragmentManager().findFragmentById(R.id.fragment_account_register);

        //TODO controllo con db
        fragment.setE_date(1521815495631L);
        fragment.setE_email("email settata");
        fragment.setE_password("password settata");
        fragment.setE_location("location settata");
        fragment.setE_name("name settato");
        fragment.setE_surname("surname set");
        fragment.setE_username("username");

        fragment.setVisibilityDate(View.GONE);
        fragment.setVisibilityBtnPassword(View.VISIBLE);
        fragment.setVisibilityPassword(View.GONE);

        fragment.setRegisterText(getResources().getString(R.string.update));
        fragment.setCancelText(getResources().getString(R.string.back));



    }

    @Override
    public void register(String username, String password, String name, String surname, String location, String email, long date) {
        //TODO controllo unicità username
        User user;
        user= User.create(username,password).withDate(date).withEmail(email).withLocation(location).withName(name).withSurname(surname);
        Log.d(TAG_LOG, "update user. Username: "+user.getUsername()+", date: "+user.getDate());
        popupMessage();
    }

    @Override
    public void cancel() {
        finish();
    }

    @Override
    public void changePassword() {
        Intent intent = new Intent(Activity_Account.this, Activity_Password.class);
        startActivity(intent);
    }
}