package com.poma.restaurant.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.poma.restaurant.R;
import com.poma.restaurant.login.Activity_Account;

public class Activity_Menu extends AppCompatActivity {
    private static final String TAG_LOG = Activity_Menu.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_LOG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button btn = (Button)findViewById(R.id.button_menu_modificaaccount);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Activity_Menu.this, Activity_Account.class);
                Log.d(TAG_LOG, "click account");

                startActivity(in);
            }
        });
    }
}