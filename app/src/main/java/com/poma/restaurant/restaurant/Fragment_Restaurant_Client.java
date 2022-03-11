package com.poma.restaurant.restaurant;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poma.restaurant.R;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.notifications.Fragment_Notification;
import com.poma.restaurant.notifications.Fragment_Notification_List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Restaurant_Client#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Restaurant_Client extends Fragment {

    private static final String TAG_LOG = Fragment_Restaurant_Client.class.getName();
    private Boolean user_access = true;
    private Boolean favourite_access = false;
    private String restaurant_id = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Restaurant_Client() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Restaurant_Client.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Restaurant_Client newInstance(String param1, String param2) {
        Fragment_Restaurant_Client fragment = new Fragment_Restaurant_Client();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_client, container, false);



        return view;
    }

    public void setRestaurant_id(String id){
        this.restaurant_id=id;
    }

    public void setUser(){
        this.user_access=true;
    }

    public void setAdmin(){
        this.user_access=false;
    }

    public void setFavouriteAccess(){
        this.favourite_access=true;
    }

    public void setNotFavouriteAccess(){
        this.favourite_access=false;
    }



    //Interfaccia
    public interface RestaurantInterface {
        public void goBack();
        public void edit_restaurant(Restaurant n);
    }

    private Fragment_Restaurant_Client.RestaurantInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Restaurant_Client.RestaurantInterface){
            listener = (Fragment_Restaurant_Client.RestaurantInterface) activity;
        }
        else {
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }
}