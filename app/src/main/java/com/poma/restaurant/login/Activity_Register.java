package com.poma.restaurant.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.poma.restaurant.R;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

public class Activity_Register extends AppCompatActivity implements Fragment_Register.RegisterInterface {
    private static final String TAG_LOG = Activity_Register.class.getName();
    private static final String USER_LOGIN_EXTRA = "com.poma.restaurant.USER_LOGIN_EXTRA ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(TAG_LOG, "on create");

    }

    @Override
    public void register(String username, String password, String name, String surname, String location, String email, long date) {
        User user;
        user= User.create(username,password).withDate(date).withEmail(email).withLocation(location).withName(name).withSurname(surname);
        Log.d(TAG_LOG, "register");

        Intent intent = new Intent();
        intent.putExtra(User.USER_DATA_EXTRA, user);
        setResult(RESULT_OK,intent);
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
}