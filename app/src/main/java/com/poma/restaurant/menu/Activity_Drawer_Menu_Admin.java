package com.poma.restaurant.menu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import com.poma.restaurant.account.Activity_Account_Admin;
import com.poma.restaurant.notifications.Activity_Notifications_Admin;
import com.poma.restaurant.restaurant.Activity_Create_Restaurant;
import com.poma.restaurant.restaurant.Activity_Restaurants_List_Admin;


public class Activity_Drawer_Menu_Admin extends AppCompatActivity {


    private static final String TAG_LOG = Activity_Drawer_Menu_Admin.class.getName();

    private DrawerLayout drawerLayout;
    private FrameLayout container;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private TextView textView_header_user_title;

    @Override
    public void setContentView(View view) {
        Log.d(TAG_LOG, "set content view");
        //setto layout del drawer
        this.drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_menu_admin, null);

        //container
        this.container = this.drawerLayout.findViewById(R.id.activityContainer);

        this.container.addView(view);

        super.setContentView(this.drawerLayout);

        //toolbar del content
        this.toolbar = drawerLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //parte a comparsa a sinistra contenente header e menu
        this.navigationView = this.drawerLayout.findViewById(R.id.nav_view_menu_user);
        this.navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        switch (item.getItemId()){
                            case R.id.nav_admin_notifications:
                                Intent in = new Intent(getApplicationContext(), Activity_Notifications_Admin.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(in);
                                break;
                            case R.id.nav_admin_profile:
                                Log.d(TAG_LOG, "click su profilo");
                                Intent in2 = new Intent(getApplicationContext(), Activity_Account_Admin.class);
                                in2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(in2);
                                break;
                            case R.id.nav_admin_logout:
                                Log.d(TAG_LOG, "click su logout");
                                broadcast_logout();
                                break;
                            case R.id.nav_admin_restaurant:
                                Log.d(TAG_LOG, "click su restaurant");
                                Intent in3 = new Intent(getApplicationContext(), Activity_Restaurants_List_Admin.class);
                                in3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(in3);
                                break;
                            case R.id.nav_admin_add_restaurant:
                                Log.d(TAG_LOG, "click su add restaurant");
                                Intent in4 = new Intent(getApplicationContext(), Activity_Create_Restaurant.class);
                                in4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(in4);
                                break;
                            case R.id.nav_admin_test:
                                Log.d(TAG_LOG, "click su add restaurant");
                                Intent in5 = new Intent(getApplicationContext(), Activity_Test.class);
                                in5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(in5);
                                break;
                        }
                        return false;
                    }

                });

        this.toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        this.drawerLayout.addDrawerListener(toggle);
        this.toggle.syncState();
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_LOG, "on create");




    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LOG, "on start");

    }

    //Metodi
    protected void allocateActivityTitle(String title){
        Log.d(TAG_LOG, "allocate activity title");
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

    protected void setTitle_header_user(String text){
        Log.d(TAG_LOG, "Set title header");

    }





}