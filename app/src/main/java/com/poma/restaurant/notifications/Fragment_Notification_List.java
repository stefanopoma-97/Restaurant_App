package com.poma.restaurant.notifications;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.poma.restaurant.R;
import com.poma.restaurant.login.Fragment_Login;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.RecyclerViewAdapter.RecyclerViewAdapter_Notification;
import com.poma.restaurant.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Notification_List#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Notification_List extends Fragment {

    private static final String TAG_LOG = Fragment_Notification_List.class.getName();
    private RecyclerView rv;
    private ArrayList<Notification> mdata;
    private FirebaseAuth mAuth;
    private Button btn_logout;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private static ListenerRegistration listener_notification;

    private RecyclerViewAdapter_Notification adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Notification_List() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Notification_List.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Notification_List newInstance(String param1, String param2) {
        Fragment_Notification_List fragment = new Fragment_Notification_List();
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
        Log.d(TAG_LOG,"on create view");
        // Inflate the layout for this fragment
        View view = (View)inflater.inflate(R.layout.fragment_notification_list, container, false);


        //RV
        this.rv = view.findViewById(R.id.RV_notification);

        // here we have created new array list and added data to it.





        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG_LOG,"on start");
/*
        //notifica 1
        for (int i = 0; i<100; i++){
            Notification n1 = new Notification("userid", "id", "Titolo not 1");
            n1.setContent("Descrizione delle notifica 1");
            long l = new Long(8407);
            n1.setDate(l);
            this.mdata.add(n1);
        }

 */     this.mdata = new ArrayList<>();
        // passo activity, array e fragment
        this.adapter = new RecyclerViewAdapter_Notification(getActivity(), mdata, Fragment_Notification_List.this);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);



        this.db = FirebaseFirestore.getInstance();
        this.mAuth= FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        if (this.currentUser!=null){
            Query query = this.db.collection("notifications")
                    .whereEqualTo("user_id", currentUser.getUid());

            this.listener_notification = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG_LOG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG_LOG, "New notify di tipo: " + dc.getDocument().getString("type"));
                                mdata.add(createNotification(dc.getDocument()));
                                setAdapter();

                                break;
                            case MODIFIED:
                                Log.d(TAG_LOG, "modify notify: " + dc.getDocument().getData());
                                int pos = removeNotificationFromData(dc.getDocument().getId());
                                mdata.add(pos, createNotification(dc.getDocument()));
                                setAdapter();
                                break;
                            case REMOVED:
                                Log.d(TAG_LOG, "Removed notify (id = "+dc.getDocument().getId()+"): " + dc.getDocument().getData());
                                removeNotificationFromData(dc.getDocument().getId());
                                setAdapter();
                                break;
                        }
                    }


                }
            });
        }





    }

    private void setAdapter(){
        /*
        this.adapter = new RecyclerViewAdapter_Notification(getActivity(), this.mdata, Fragment_Notification_List.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        this.rv.setLayoutManager(linearLayoutManager);
        this.rv.setAdapter(adapter);*/
        this.adapter.notifyDataSetChanged();

    }

    private int removeNotificationFromData(String id){
        int index = 0;
        for (Notification n:this.mdata){

            if (n.getId().equals(id)){
                this.mdata.remove(n);
                return index;
            }
            index ++;
        }
        return index;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG_LOG,"un register receiver");
        this.listener_notification.remove();
    }

    private Notification createNotification(QueryDocumentSnapshot d){
        Log.d(TAG_LOG, "Creando una notifica");
        Notification n = new Notification(mAuth.getCurrentUser().getUid(), d.getId(), d.getString("type"));
        //n.setDate((long)d.get("date"));
        n.setContent((String)d.get("content"));
        n.setRead((Boolean)d.get("read"));
        Log.d(TAG_LOG, "Notifica creata correttamente, user_id: "+n.getUser_id()+", id: "+n.getId()+", type: "+n.getType());
        return n;
    }


    //Interfaccia
    //Activity deve implementare i metodi specificati
    public interface NotificationListInterface {
        public void click_notification(String id);
        public void cancel();
    }

    private Fragment_Notification_List.NotificationListInterface listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Notification_List.NotificationListInterface){
            listener = (Fragment_Notification_List.NotificationListInterface) activity;
        }
        else {
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }
}