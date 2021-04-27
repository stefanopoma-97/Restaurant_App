package com.poma.restaurant.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.poma.restaurant.R;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

public class Activity_First_Access extends AppCompatActivity implements Fragment_Access.FirstAccessInterface, Fragment_Access_Admin.FirstAccessAdminInterface {

    private static final String TAG_LOG = Activity_First_Access.class.getName();
    private static final String USER_LOGIN_EXTRA = "com.poma.restaurant.USER_LOGIN_EXTRA ";


    private static final int LOGIN_REQUEST_ID = 1;
    private static final int REGISTRATION_REQUEST_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_access);
        Log.d(TAG_LOG,"Start activity");

    }

    @Override
    public void login(Boolean user) {
        Log.d(TAG_LOG, "Login");
        final Intent intent = new Intent(this, Activity_Login.class);
        intent.putExtra(USER_LOGIN_EXTRA, user);
        startActivityForResult(intent, LOGIN_REQUEST_ID);
        Log.d(TAG_LOG, "send Intent for result. Login with user: "+user);


    }

    @Override
    public void register(Boolean user) {
        Log.d(TAG_LOG, "Register");
        final Intent intent = new Intent(this, Activity_Register.class);
        intent.putExtra(USER_LOGIN_EXTRA, user);
        startActivityForResult(intent, REGISTRATION_REQUEST_ID);
        Log.d(TAG_LOG, "send Intent for result. Login with user: "+user);

    }

    @Override
    public void anonymous_access(Boolean user) {

    }

    @Override
    public void login_admin(Boolean user) {
        Log.d(TAG_LOG, "Login Admin");
        final Intent intent = new Intent(this, Activity_Login.class);
        intent.putExtra(USER_LOGIN_EXTRA, user);
        startActivityForResult(intent, LOGIN_REQUEST_ID);
        Log.d(TAG_LOG, "send Intent for result. Login with user: "+user);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        final User user;
        final Intent mainIntent;
        if(requestCode == LOGIN_REQUEST_ID)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    Log.d(TAG_LOG, "Return from login (user): OK");
                    user = data.getParcelableExtra(User.USER_DATA_EXTRA);
                    mainIntent = new Intent(Activity_First_Access.this, Activity_Menu.class);
                    startActivity(mainIntent);
                    Log.d(TAG_LOG, "start menù with user: "+user.getUsername());

                    //finish();
                    break;
                case Action.RESULT_OK_ADMIN:
                    Log.d(TAG_LOG, "Return from login (admin): OK");
                    user = data.getParcelableExtra(User.USER_DATA_EXTRA);
                    mainIntent = new Intent(Activity_First_Access.this, Activity_Menu.class);
                    startActivity(mainIntent);
                    Log.d(TAG_LOG, "start menù with user:"+user.getUsername());

                    //finish();
                    break;

                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from login: CANCELED");
                    break;
            }
        } else if(requestCode == REGISTRATION_REQUEST_ID)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    /*final UserModel userModel = (UserModel)data.getParcelableExtra(UserModel.USER_DATA_EXTRA);
                    final Intent detailIntent = new Intent(Action.SHOW_USER_ACTION);
                    Log.d(TAG_LOG,"Registration completed! Username:"+userModel.getUsername());
                    detailIntent.putExtra(UserModel.USER_DATA_EXTRA,userModel);
                    Log.d(TAG_LOG,"Put Extra in intent");
                    startActivity(detailIntent);
                    Log.d(TAG_LOG,"Start Intent");
                    finish();*/
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
    }
}