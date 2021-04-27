package com.poma.restaurant.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.poma.restaurant.R;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

public class Activity_Login extends AppCompatActivity implements Fragment_Login.LoginInterface{
    private static final String TAG_LOG = Activity_Login.class.getName();
    private static final String USER_LOGIN_EXTRA = "com.poma.restaurant.USER_LOGIN_EXTRA ";

    private Boolean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG_LOG, "on create");

        TextView title = (TextView)findViewById(R.id.textview_title_login);
        View view = findViewById(R.id.view_rectangle_login);
        Button btn_login = (Button)findViewById(R.id.login_confirm);

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
    public void login(String username, String password) {
        Log.d(TAG_LOG, "inizio metodo login, con user: "+this.user);

        Fragment_Login fragment_login = (Fragment_Login)
                getSupportFragmentManager().findFragmentById(R.id.fragment_login);
        final String DUMMY_USERNAME = "stefano";
        final String DUMMY_PASSWORD = "poma";

        if(this.user){ //se ho fatto accesso a questa activity per login utente
            if(DUMMY_USERNAME.equals(username) && DUMMY_PASSWORD.equals(password)){
                User user;
                user= User.create(username,password);
                Log.d(TAG_LOG, "login con utente fatto");

                Intent intent = new Intent();
                intent.putExtra(User.USER_DATA_EXTRA, user);
                setResult(RESULT_OK,intent);
                finish();
            }
            fragment_login.setError(getResources().getString(R.string.wrong_credentials));
        }
        else { // se ho fatto accesso per login admin
            Log.d(TAG_LOG, "utente admin");

            if(username.equals("admin") && password.equals("admin")){
                Log.d(TAG_LOG, "utente admin corretto");

                User user;
                user= User.create(username,password);
                Log.d(TAG_LOG, "login con admin fatto");

                Intent intent = new Intent();
                intent.putExtra(User.USER_DATA_EXTRA, user);
                setResult(Action.RESULT_OK_ADMIN,intent);
                finish();
            }
            Log.d(TAG_LOG, "utente admin non corretto");

            fragment_login.setError(getResources().getString(R.string.wrong_credentials));
        }

    }

    @Override
    public void cancel() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }
}