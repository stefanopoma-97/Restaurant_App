package com.poma.restaurant.login;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.poma.restaurant.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Login extends Fragment {
    private static final String TAG_LOG = Fragment_Login.class.getName();
    private Button btn_cancel;
    private Button btn_login;
    private EditText e_username;
    private EditText e_password;
    private EditText e_email;
    private TextView error;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Login newInstance(String param1, String param2) {
        Fragment_Login fragment = new Fragment_Login();
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
        View view = (View)inflater.inflate(R.layout.fragment_login, container, false);

        this.btn_cancel = (Button)view.findViewById(R.id.button_cancel_loginform);
        this.btn_login = (Button)view.findViewById(R.id.login_confirm);
        this.e_username = (EditText)view.findViewById(R.id.username_loginform);
        this.e_password = (EditText)view.findViewById(R.id.password_loginform);
        this.e_email = (EditText)view.findViewById(R.id.email_loginform);

        this.error = (TextView)view.findViewById(R.id.textview_alert_message_loginform);



        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(checkFields()){
                    String username = e_username.getText().toString();
                    String password = e_password.getText().toString();
                    String email = e_email.getText().toString();

                    Log.d(TAG_LOG,"fragment manda comando login a activity");
                    listener.login(email, password);

                }
                else{
                    return;
                }
            }
        });


        return view;
    }

    //Interfaccia
    //Activity deve implementare i metodi specificati
    public interface LoginInterface {
        public void login(String email, String password);
        public void cancel();
    }

    private Fragment_Login.LoginInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Login.LoginInterface){
            listener = (Fragment_Login.LoginInterface) activity;
        }
        else {
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }

    public void setError(String text){
        this.error.setText(text);
    }

    private Boolean checkFields (){
        String email = this.e_email.getText().toString();
        String password = this.e_password.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            error.setText(getResources().getString(R.string.email_mandatory));
            error.setVisibility(View.VISIBLE);
            return false;
        }

        if(TextUtils.isEmpty(password))
        {
            error.setText(getResources().getString(R.string.password_mandatory));
            error.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }
}