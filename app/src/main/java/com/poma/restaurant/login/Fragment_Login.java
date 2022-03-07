package com.poma.restaurant.login;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

    //state
    private static final String ERROR_STRING_KEY_FRAGMENT_LOGIN = "com.poma.restaurant.ERROR_STRING_KEY_FRAGMENT_LOGIN";
    private static String error_state ="";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
        Log.d(TAG_LOG,"on create fragment");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG_LOG,"on create view fragment");
        // Inflate the layout for this fragment
        View view = (View)inflater.inflate(R.layout.fragment_login, container, false);

        this.btn_cancel = (Button)view.findViewById(R.id.button_cancel_loginform);
        this.btn_login = (Button)view.findViewById(R.id.login_confirm);
        this.e_password = (EditText)view.findViewById(R.id.password_loginform);
        this.e_email = (EditText)view.findViewById(R.id.email_loginform);

        this.error = (TextView)view.findViewById(R.id.textview_alert_message_loginform);


        //invoca metodo "cancel()"
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel();
            }
        });

        //Invoca metodo "Login()"
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(checkFields()){
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

    //State
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(ERROR_STRING_KEY_FRAGMENT_LOGIN, this.error.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG_LOG,"Save state: "+ERROR_STRING_KEY_FRAGMENT_LOGIN+" valore: "+this.error.getText().toString());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG_LOG,"on Activity Create");

        if (savedInstanceState != null){
            String errore = savedInstanceState.getString(ERROR_STRING_KEY_FRAGMENT_LOGIN);
            if (errore==""){
                this.error_state="";
            }
            else {
                this.error_state=errore;
            }

            setErrorState(this.error_state);

            Log.d(TAG_LOG,"Retrive state: "+ERROR_STRING_KEY_FRAGMENT_LOGIN+" valore: "+errore);
        }
        else {
            Log.d(TAG_LOG,"Retrive state: "+ERROR_STRING_KEY_FRAGMENT_LOGIN+" valore: null");
        }

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
        Log.d(TAG_LOG,"Ricevuto errore e scritto");
        this.error.setText(text);
        this.error.setVisibility(View.VISIBLE);
        this.error.requestFocus();

    }

    private void setErrorState(String text){
        if (text == ""){
            this.error.setText("");
            this.error.setVisibility(View.INVISIBLE);
        }
        else {
            this.error.setText(text);
            this.error.setVisibility(View.VISIBLE);
        }
    }

    //Metodo per controllare il completamento della form senza usare il DB
    private Boolean checkFields (){
        String email = this.e_email.getText().toString();
        String password = this.e_password.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            error.setText(getResources().getString(R.string.email_mandatory));
            error.setVisibility(View.VISIBLE);
            error.requestFocus();
            Log.d(TAG_LOG,"email vuota");
            return false;
        }

        if(TextUtils.isEmpty(password))
        {
            error.setText(getResources().getString(R.string.password_mandatory));
            error.setVisibility(View.VISIBLE);
            error.requestFocus();
            Log.d(TAG_LOG,"password vuota");
            return false;
        }
        return true;
    }
}