package com.poma.restaurant.notifications;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.RecyclerViewAdapter.RecyclerViewAdapter_Notification;
import com.poma.restaurant.model.User;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Notification_List#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Notification_List extends Fragment {

    private static final String TAG_LOG = Fragment_Notification_List.class.getName();
    private static final String SEARCH_KEY_FRAGMENT_NOTIFICATIONS_LIST = "com.poma.restaurant.SEARCH_KEY_FRAGMENT_NOTIFICATIONS_LIST";


    private RecyclerView rv;
    private ArrayList<Notification> mdata;
    private FirebaseAuth mAuth;
    private Button btn_logout;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private static ListenerRegistration listener_notification;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Boolean order = false;

    private RecyclerViewAdapter_Notification adapter;
    private TextView textView_filter;
    private SearchView searchView = null;
    private TextView textView_no_result;
    private String search = "";



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
        this.rv = view.findViewById(R.id.RV_fragment_favourite);
        this.textView_filter = view.findViewById(R.id.textview_filter_notifications_list);
        this.searchView = view.findViewById(R.id.search_view_fragment_favourite);
        this.textView_no_result = view.findViewById(R.id.textview_no_result_notifications_list);
        this.textView_no_result.setVisibility(View.INVISIBLE);


        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_fragment_favourite);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecycler();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        this.textView_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderRecycler();
            }
        });

        return view;
    }

    //State
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(SEARCH_KEY_FRAGMENT_NOTIFICATIONS_LIST, this.searchView.getQuery().toString());
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG_LOG,"Save state: "+SEARCH_KEY_FRAGMENT_NOTIFICATIONS_LIST+" valore: "+this.searchView.getQuery().toString());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG_LOG,"on Activity Create");

        if (savedInstanceState != null){
            this.search = savedInstanceState.getString(SEARCH_KEY_FRAGMENT_NOTIFICATIONS_LIST);
            //searchView.setQuery(search, true);


            Log.d(TAG_LOG,"Retrive state: "+SEARCH_KEY_FRAGMENT_NOTIFICATIONS_LIST+" valore: "+search);
        }
        else {
            Log.d(TAG_LOG,"Retrive state: "+SEARCH_KEY_FRAGMENT_NOTIFICATIONS_LIST+" valore: null");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG_LOG,"on start");

        this.db = FirebaseFirestore.getInstance();
        this.mAuth= FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();

        this.mdata = new ArrayList<>();
        getAllNotifications();


        setSearchView();

    }

   private void setSearchView(){
       Log.d(TAG_LOG,"Set Search View");
       this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String s) {
               Log.d(TAG_LOG,"on query submit");
               if (s.length() > 0) {
                   //Log.d(TAG_LOG,"stringa > 0");
                   filter(String.valueOf(s));
                   searchView.clearFocus();
               } else {
                   //Log.d(TAG_LOG,"stringa < 0");
                   getAllNotifications();
               }
               return true;
           }
           @Override
           public boolean onQueryTextChange(String s) {
               Log.d(TAG_LOG,"on query text change");
               if (s.length() > 0) {
                   //Log.d(TAG_LOG,"stringa > 0");
                   filter(String.valueOf(s));
               } else {
                   //Log.d(TAG_LOG,"stringa < 0");
                   getAllNotifications();
               }
               return true;
           }
       });

       Log.d(TAG_LOG,"recupero valore da mettere in search");
       if (this.search.equals(""))
           searchView.clearFocus();
       else{
           Log.d(TAG_LOG,"Set query");
           searchView.setQuery(this.search, true);
       }


    }

    private void getAllNotifications(){
        Log.d(TAG_LOG,"GET ALL notification");
        this.mdata = new ArrayList<>();
        // passo activity, array e fragment
        this.adapter = new RecyclerViewAdapter_Notification(getActivity(), mdata, Fragment_Notification_List.this);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);
        this.textView_no_result.setVisibility(View.VISIBLE);
        Log.d(TAG_LOG,"GET ALL no result text view");


        Query query = this.db.collection("notifications")
                .whereEqualTo("user_id", currentUser.getUid());

        this.listener_notification = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                Log.d(TAG_LOG,"GET ALL on Event");
                if (e != null) {
                    Log.w(TAG_LOG, "Listen failed.", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    Log.d(TAG_LOG,"GET ALL get document changes");
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG_LOG, "New notify di tipo: " + dc.getDocument().getString("type"));
                            mdata.add(createNotification(dc.getDocument()));
                            setAdapterChange();

                            if (searchView.getQuery().toString().equals(""))
                                Log.d(TAG_LOG,"Controllo search view, è vuota non faccio nulla");
                            else{
                                Log.d(TAG_LOG,"Controllo search view, contine qualcosa, applico filtro");
                                filter(searchView.getQuery().toString());
                            }

                            break;
                        case MODIFIED:
                            Log.d(TAG_LOG, "modify notify: " + dc.getDocument().getData());
                            int pos = removeNotificationFromData(dc.getDocument().getId());
                            mdata.add(pos, createNotification(dc.getDocument()));
                            setAdapterChange();

                            if (searchView.getQuery().toString().equals(""))
                                Log.d(TAG_LOG,"Controllo search view, è vuota non faccio nulla");
                            else{
                                Log.d(TAG_LOG,"Controllo search view, contine qualcosa, applico filtro");
                                filter(searchView.getQuery().toString());
                            }
                            break;
                        case REMOVED:
                            Log.d(TAG_LOG, "Removed notify (id = "+dc.getDocument().getId()+"): " + dc.getDocument().getData());
                            removeNotificationFromData(dc.getDocument().getId());
                            setAdapterChange();


                            if (searchView.getQuery().toString().equals(""))
                                Log.d(TAG_LOG,"Controllo search view, è vuota non faccio nulla");
                            else{
                                Log.d(TAG_LOG,"Controllo search view, contine qualcosa, applico filtro");
                                filter(searchView.getQuery().toString());
                            }
                            break;
                    }
                }
                //Dopo aver trovato gli elementi definisco la search view


            }
        });

    }
    //Aggiornamento manuale
    private void updateRecycler() {
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);

    }

    //ordinamento
    private void orderRecycler() {
        if (this.order){
            Collections.sort(this.mdata, Collections.reverseOrder());
            this.order = false;
        }
        else {
            Collections.sort(this.mdata);
            this.order = true;
        }


        setAdapterChange();
    }

    private void setAdapterChange(){
        Log.d(TAG_LOG,"set Adapter");

        this.adapter.notifyDataSetChanged();
        if(adapter.getItemCount()==0)
            this.textView_no_result.setVisibility(View.VISIBLE);
        else
            this.textView_no_result.setVisibility(View.INVISIBLE);

        Log.d(TAG_LOG,"Adapter get position: "+adapter.getItemCount());



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
        n.setDate((long)d.get("date"));
        n.setContent((String)d.get("content"));
        n.setRead((Boolean)d.get("read"));
        Log.d(TAG_LOG, "Notifica creata correttamente, user_id: "+n.getUser_id()+", id: "+n.getId()+", type: "+n.getType()+", read: "+n.getRead());
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



    private void filter(String text) {
        Log.d(TAG_LOG,"filter");
        //this.listener_notification.remove();
        ArrayList<Notification> filteredList = new ArrayList<>();


        for (Notification item : this.mdata) {

            if (item.getType().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            } else if (item.getContent().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
            if (filteredList.isEmpty()) {
                //Toast.makeText(getActivity(), "No data found...", Toast.LENGTH_SHORT).show();
                //Log.d(TAG_LOG,"filter list empty: "+filteredList.size());
            }
            else {
                //Log.d(TAG_LOG,"filter list not empty: "+filteredList.size());
            }


            this.adapter = new RecyclerViewAdapter_Notification(getActivity(), filteredList, Fragment_Notification_List.this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rv.setLayoutManager(linearLayoutManager);
            rv.setAdapter(adapter);

            setAdapterChange();
        }
    }

}