package com.poma.restaurant.notifications;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Notification;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Notification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Notification extends Fragment {
    private static final String TAG_LOG = Fragment_Notification_List.class.getName();

    private String notification_id=null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Boolean dialog_box = false;
    private static final String DIALOG_BOX_OPEN_KEY_FRAGMENT_NOTIFICATION= "com.poma.restaurant.DIALOG_BOX_OPEN_KEY_FRAGMENT_NOTIFICATION";



    private Button btn_back;
    private Button btn_view;
    private TextView textView_delete;
    private TextView textView_title;
    private TextView textView_description;
    private TextView textView_date;
    private Notification notification;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Notification() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Notification.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Notification newInstance(String param1, String param2) {
        Fragment_Notification fragment = new Fragment_Notification();
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
        Log.d(TAG_LOG,"on create view");
        View view = (View)inflater.inflate(R.layout.fragment_notification, container, false);


        this.btn_back = (Button)view.findViewById(R.id.btn_single_notification_goback);
        this.btn_view = (Button)view.findViewById(R.id.btn_single_notification_view);
        this.textView_title = (TextView)view.findViewById(R.id.textview_single_notification_title);
        this.textView_description = (TextView)view.findViewById(R.id.textview_single_notification_description);
        this.textView_delete = (TextView)view.findViewById(R.id.textview_single_notification_delete);
        this.textView_date = (TextView)view.findViewById(R.id.textview_single_notification_date);

        this.btn_back.setVisibility(View.GONE);
        this.btn_view.setVisibility(View.GONE);
        this.textView_title.setVisibility(View.GONE);
        this.textView_description.setVisibility(View.GONE);
        this.textView_delete.setVisibility(View.GONE);
        this.textView_date.setVisibility(View.GONE);

        this.mAuth= FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();

        this.notification = new Notification();


        this.btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        this.btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view();
            }
        });

        this.textView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotification();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG_LOG,"on start");
        if(this.notification_id != null){
            retrive_notification_info();
        }


    }

    //Metodi

    private void retrive_notification_info(){


        DocumentReference docRef = this.db.collection("notifications").document(this.notification_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d(TAG_LOG, "DocumentSnapshot data: " + data);


                        notification.setUser_id((String) data.get("user_id"));
                        notification.setType((String) data.get("type"));
                        notification.setContent((String) data.get("content"));
                        notification.setUseful_id((String) data.get("useful_id"));


                        textView_title.setText((String) data.get("type"));
                        textView_description.setText((String) data.get("content"));
                        SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis((long) data.get("date"));
                        textView_date.setText(formatter.format(calendar.getTime()));



                        //progressBar.setVisibility(View.INVISIBLE);


                    } else {
                        Log.d(TAG_LOG, "No such document");
                    }
                } else {
                    Log.d(TAG_LOG, "get failed with ", task.getException());
                }
            }
        });
        set_read_true();
        this.btn_back.setVisibility(View.VISIBLE);
        this.btn_view.setVisibility(View.VISIBLE);
        this.textView_title.setVisibility(View.VISIBLE);
        this.textView_description.setVisibility(View.VISIBLE);
        this.textView_delete.setVisibility(View.VISIBLE);
        this.textView_date.setVisibility(View.VISIBLE);
    }

    private void set_read_true(){
        Map<String, Object> updates = new HashMap<>();
        updates.put("read", true);

        DocumentReference document = db.collection("notifications").document(this.notification_id);
        document.set(updates, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG_LOG, "aggiorno notific");

                        }
                        else{
                            Log.d(TAG_LOG, "problemi aggiornamento notifica");

                        }
                    }
                });
    }

    private void delete(){
        DocumentReference document = db.collection("notifications").document(this.notification_id);
        document.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG_LOG, "notifica eliminata");

                        }
                        else{
                            Log.d(TAG_LOG, "problemi eliminazione notifica");

                        }
                    }
                });
    }

    public void setNotification_id(String id){

        this.notification_id=id;
        Log.d(TAG_LOG, "Set id notification: "+this.notification_id);
        retrive_notification_info();
        set_read_true();
    }

    private void goBack(){
        //Toast.makeText(getContext(), "Go back", Toast.LENGTH_SHORT).show();
        Log.d(TAG_LOG, "Go back");
        listener.goBack();
        return;
    }

    private void view(){
        Log.d(TAG_LOG, "View");
        listener.view(this.notification);
        return;
    }

    private void deleteNotification(){
        this.dialog_box = true;
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getContext())
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_notification_delete)
                //.setIcon(R.drawable.ic_baseline_delete_24_black)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        delete();
                        listener.goBack();
                        Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();


                        dialog_box = false;
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog_box = false;
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog_box = false;
                    }
                })
                .create();
        myQuittingDialogBox.show();
        myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        myQuittingDialogBox.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.red));


    }


    //State
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(DIALOG_BOX_OPEN_KEY_FRAGMENT_NOTIFICATION, this.dialog_box);
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG_LOG,"on Activity Create");
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG_LOG,"on Activity Create");

        if (savedInstanceState != null){
            Boolean b = savedInstanceState.getBoolean(DIALOG_BOX_OPEN_KEY_FRAGMENT_NOTIFICATION);
            if (b){
                deleteNotification();
            }

        }
        else {
            Log.d(TAG_LOG,"Retrive state: "+DIALOG_BOX_OPEN_KEY_FRAGMENT_NOTIFICATION+" valore: null");
        }


    }




    //Interfaccia
    //Activity deve implementare i metodi specificati
    public interface NotificationInterface {
        public void goBack();
        public void view(Notification n);
    }

    private Fragment_Notification.NotificationInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Notification.NotificationInterface){
            listener = (Fragment_Notification.NotificationInterface) activity;
        }
        else {
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }
}