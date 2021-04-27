package com.poma.restaurant.login;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.poma.restaurant.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Access#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Access extends Fragment {
    private static final String TAG_LOG = Fragment_Access.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Access() {
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
    public static Fragment_Access newInstance(String param1, String param2) {
        Fragment_Access fragment = new Fragment_Access();
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

        View view= (View)inflater.inflate(R.layout.fragment_access, container, false);


        Button button = (Button) view.findViewById(R.id.login_button);
        Button btn_register = (Button) view.findViewById(R.id.register_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.login(true);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.register(true);
            }
        });


        return view;
    }

    //Interfaccia
    //Activity deve implementare i metodi specificati
    public interface FirstAccessInterface {
        public void login(Boolean user);
        public void register(Boolean user);
        public void anonymous_access(Boolean user);
    }

    private FirstAccessInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof FirstAccessInterface){
            listener = (FirstAccessInterface) activity;
        }
        else {
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }



}