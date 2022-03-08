package com.poma.restaurant.menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.poma.restaurant.R;
import com.poma.restaurant.account.Activity_Account;
import com.poma.restaurant.utilities.MyApplication;


import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;


public class Activity_Drawer_Menu_User extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private static final String TAG_LOG = Activity_Drawer_Menu_User.class.getName();

    @Override
    public void setContentView(View view) {
        //setto layout del drawer
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_menu_user, null);

        //container
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);

        container.addView(view);

        super.setContentView(drawerLayout);

        //toolbar del content
        Toolbar toolbar = drawerLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //parte a comparsa a sinistra contenente header e menu
        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view_menu_user);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        switch (item.getItemId()){
                            case R.id.nav_user_notifications:
                                //startActivity(new Intent(this, ));
                                break;
                            case R.id.nav_user_profile:
                                Log.d(TAG_LOG, "click su profilo");
                                startActivity(new Intent(getApplicationContext(), Activity_Account.class));
                                break;
                            case R.id.nav_user_logout:
                                Log.d(TAG_LOG, "click su logout");
                                broadcast_logout();
                                break;
                        }
                        return false;
                    }

                });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.d(TAG_LOG, "on create");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "on start");
    }

    //Metodi
    protected void allocateActivityTitle(String title){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
            }

        }

    //broadcast per logout
    private void broadcast_logout(){
        Log.d(TAG_LOG, "Logout - inizio procedura");
        Intent intent = new Intent();
        intent.setAction("com.poma.restaurant.broadcastreceiversandintents.BROADCAST_LOGOUT");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
        Log.d(TAG_LOG, "Broadcast mandato");
    }



}