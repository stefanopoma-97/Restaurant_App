package com.poma.restaurant.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.poma.restaurant.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Access_Admin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Access_Admin extends Fragment {
    private static final String TAG_LOG = Fragment_Access.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Access_Admin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_First.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Access_Admin newInstance(String param1, String param2) {
        Fragment_Access_Admin fragment = new Fragment_Access_Admin();
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
        Log.d(TAG_LOG,"onCreate Fragment");

       View view = (View) inflater.inflate(R.layout.fragment_access_admin, container, false);


        TextView text = (TextView) view.findViewById(R.id.access_admin);
        text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    listener.login_admin(false);
                }
                Log.i(TAG_LOG, "login admin");
                return false;
            }
        });


        return view;
    }

    //Interfaccia
    //Activity deve implementare i metodi specificati
    public interface FirstAccessAdminInterface {
        public void login_admin(Boolean user);
    }

    private Fragment_Access_Admin.FirstAccessAdminInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Access_Admin.FirstAccessAdminInterface){
            listener = (FirstAccessAdminInterface) activity;
        }
        else {
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }


}