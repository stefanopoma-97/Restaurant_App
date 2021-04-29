package com.poma.restaurant.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.poma.restaurant.R;

public class Activity_Password extends AppCompatActivity implements Fragment_Password.PasswordInterface {
    private static final String TAG_LOG = Activity_Account.class.getName();
    private Fragment_Password fragment;

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

        //fragment.buttonSaveSetEnable(false);

    }

    @Override
    public void save(String new_password, String old_password) {
        Log.d(TAG_LOG, "save");
        //TODO salvo pass e cotrollo che corrisponda a quella esistente

        if(old_password.equals("poma")){
            Log.d(TAG_LOG, "old password ok");

            Toast.makeText(Activity_Password.this, getResources().getString(R.string.password_saved), Toast.LENGTH_SHORT).show();

            finish();
        }
        else {
            Log.d(TAG_LOG, "old password wrong");
            this.fragment.setError(getResources().getString(R.string.error_old_pass));
        }




    }

    @Override
    public void cancel() {
        Log.d(TAG_LOG, "cancel");
        finish();
    }
}