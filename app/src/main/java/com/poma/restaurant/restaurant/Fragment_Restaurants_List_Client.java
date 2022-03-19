package com.poma.restaurant.restaurant;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

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
import com.poma.restaurant.filter.Activity_Filter;
import com.poma.restaurant.login.Activity_First_Access;
import com.poma.restaurant.login.Activity_Login;
import com.poma.restaurant.menu.Activity_Menu;
import com.poma.restaurant.menu.Activity_Menu_Admin;
import com.poma.restaurant.model.RecyclerViewAdapter.RecyclerViewAdapter_Restaurant;
import com.poma.restaurant.model.Restaurant;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.Action;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Restaurants_List_Client#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Restaurants_List_Client extends Fragment {

    //Per capire cosa mostrare
    private Boolean admin = false;
    private Boolean anonymous = false;
    private String admin_id;

    private static final String TAG_LOG = Fragment_Restaurants_List_Client.class.getName();
    private static final String SEARCH_KEY_FRAGMENT_RESTAURANTS_LIST = "com.poma.restaurant.SEARCH_KEY_FRAGMENT_RESTAURANTS_LIST";

    private static final String CITY_KEY_FRAGMENT_RESTAURANTS_LIST = "com.poma.restaurant.CITY_KEY_FRAGMENT_RESTAURANTS_LIST";
    private static final String CATEGORY_KEY_FRAGMENT_RESTAURANTS_LIST = "com.poma.restaurant.CATEGORY_KEY_FRAGMENT_RESTAURANTS_LIST";
    private static final String VOTE_KEY_FRAGMENT_RESTAURANTS_LIST = "com.poma.restaurant.VOTE_KEY_FRAGMENT_RESTAURANTS_LIST";


    private RecyclerView rv;
    private ArrayList<Restaurant> mdata;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private static ListenerRegistration listener_notification;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewAdapter_Restaurant adapter;
    private SearchView searchView = null;

    private TextView textView_no_result;
    private String search = "";
    private Button btn_filter;

    //Filter
    private String city_filter = "";
    private Float vote_filter = new Float(0);
    private ArrayList<String> categories_filter = new ArrayList<String>();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Restaurants_List_Client() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Restaurants_List_Client.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Restaurants_List_Client newInstance(String param1, String param2) {
        Fragment_Restaurants_List_Client fragment = new Fragment_Restaurants_List_Client();
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
        View view= inflater.inflate(R.layout.fragment_restaurants_list__client, container, false);

        //RV
        this.rv = view.findViewById(R.id.RV_fragment_favourite);


        this.searchView = view.findViewById(R.id.search_view_fragment_favourite);
        this.textView_no_result = view.findViewById(R.id.textview_no_result_restaurants_list);
        this.textView_no_result.setVisibility(View.INVISIBLE);
        this.btn_filter = view.findViewById(R.id.btn_filter_restaurants_list);

        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_fragment_favourite);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //updateRecycler();
                swipeRefreshLayout.setRefreshing(false);
            }
        });




        return view;
    }

    //State
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(SEARCH_KEY_FRAGMENT_RESTAURANTS_LIST, this.searchView.getQuery().toString());
        savedInstanceState.putString(CITY_KEY_FRAGMENT_RESTAURANTS_LIST, this.city_filter);
        savedInstanceState.putStringArrayList(CATEGORY_KEY_FRAGMENT_RESTAURANTS_LIST, this.categories_filter);
        savedInstanceState.putFloat(VOTE_KEY_FRAGMENT_RESTAURANTS_LIST, this.vote_filter);

        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG_LOG,"Save state: "+SEARCH_KEY_FRAGMENT_RESTAURANTS_LIST+" valore: "+this.searchView.getQuery().toString());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG_LOG,"on Activity Create");

        if (savedInstanceState != null){
            this.search = savedInstanceState.getString(SEARCH_KEY_FRAGMENT_RESTAURANTS_LIST);
            this.city_filter = savedInstanceState.getString(CITY_KEY_FRAGMENT_RESTAURANTS_LIST);
            this.vote_filter = savedInstanceState.getFloat(VOTE_KEY_FRAGMENT_RESTAURANTS_LIST);
            this.categories_filter = new ArrayList<>();
            this.categories_filter = savedInstanceState.getStringArrayList(CATEGORY_KEY_FRAGMENT_RESTAURANTS_LIST);


            Log.d(TAG_LOG,"Retrive state: "+SEARCH_KEY_FRAGMENT_RESTAURANTS_LIST+" valore: "+search);
        }
        else {
            Log.d(TAG_LOG,"Retrive state: "+SEARCH_KEY_FRAGMENT_RESTAURANTS_LIST+" valore: null");
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG_LOG,"on start");

        this.db = FirebaseFirestore.getInstance();
        this.mAuth= FirebaseAuth.getInstance();
        if (this.listener_notification!=null)
            this.listener_notification.remove();

        this.currentUser = mAuth.getCurrentUser();

        this.mdata = new ArrayList<>();

        getRestaurants();

        setSearchView();



        this.btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getContext(), Activity_Filter.class);
                intent.putExtra(Action.FILTER_CITY_EXTRA, city_filter);
                intent.putExtra(Action.FILTER_VOTE_EXTRA, vote_filter);
                intent.putExtra(Action.FILTER_CATEGORY_EXTRA, categories_filter);
                startActivityForResult(intent, Action.FILTER_REQUEST);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d(TAG_LOG, "On activity result");
        //final User user;
        final Intent mainIntent;

        //risposta ad un login
        if(requestCode == Action.FILTER_REQUEST) {
            switch (resultCode)
            {
                case RESULT_OK:
                    Log.d(TAG_LOG, "Return from filter OK");

                    Log.d(TAG_LOG, "Città: "+data.getStringExtra(Action.FILTER_CITY_EXTRA));
                    Log.d(TAG_LOG, "Vote: "+data.getFloatExtra(Action.FILTER_VOTE_EXTRA, new Float(0)));
                    Log.d(TAG_LOG, "Category: "+data.getStringArrayListExtra(Action.FILTER_CATEGORY_EXTRA));

                    this.city_filter=data.getStringExtra(Action.FILTER_CITY_EXTRA);
                    this.vote_filter=data.getFloatExtra(Action.FILTER_VOTE_EXTRA, new Float(0));
                    this.categories_filter = new ArrayList<>();
                    this.categories_filter=data.getStringArrayListExtra(Action.FILTER_CATEGORY_EXTRA);

                    Log.d(TAG_LOG, "Città: "+this.city_filter);
                    Log.d(TAG_LOG, "Vote: "+this.vote_filter);
                    Log.d(TAG_LOG, "Category: "+this.categories_filter);

                    onStart();

                    break;

                case RESULT_CANCELED:
                    Log.d(TAG_LOG, "Return from login: CANCELED");
                    break;
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG_LOG,"un register receiver");
        this.listener_notification.remove();
    }

    public void setAdmin(Boolean b){
        this.admin = b;
    }
    public void setAnonymous(Boolean b){
        this.btn_filter.setVisibility(View.INVISIBLE);
        this.anonymous = b;
    }
    public void setAdminID(String admin_id){

        this.admin_id = admin_id;
    }


    private void getRestaurants(){
        Log.d(TAG_LOG,"Get restaurants... quali?");
        if (this.anonymous==true){
            Log.d(TAG_LOG,"tutti (anonymous)");
            getAllRestaurants();
        }
        else if (this.admin==false){
            Log.d(TAG_LOG,"tutti (user");
            getAllRestaurants();
        }
        else if (this.admin==true){
            Log.d(TAG_LOG,"admin - admin id:"+this.mAuth.getCurrentUser());
            getAdminRestaurants();
        }

    }

    private void getAllRestaurants(){
        Log.d(TAG_LOG,"get all restaurants");
        this.mdata = new ArrayList<>();

        createAdapter(mdata);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);
        this.textView_no_result.setVisibility(View.VISIBLE);


        Query query = getQueryFiltered();

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
                            mdata.add(createRestaurant(dc.getDocument()));
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
                            int pos = removeRestaurantFromData(dc.getDocument().getId());
                            mdata.add(pos, createRestaurant(dc.getDocument()));
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
                            removeRestaurantFromData(dc.getDocument().getId());
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


            }
        });

    }

    private void getAdminRestaurants(){
        Log.d(TAG_LOG,"get all admin restaurants");
        this.mdata = new ArrayList<>();

        createAdapter(mdata);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);
        this.textView_no_result.setVisibility(View.VISIBLE);

        if (this.currentUser!=null){
            Query query = this.db.collection("restaurants").whereEqualTo("admin_id",this.mAuth.getCurrentUser().getUid());

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
                                mdata.add(createRestaurant(dc.getDocument()));
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
                                int pos = removeRestaurantFromData(dc.getDocument().getId());
                                mdata.add(pos, createRestaurant(dc.getDocument()));
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
                                removeRestaurantFromData(dc.getDocument().getId());
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


                }
            });
        }
    }

    private Query getQueryFiltered(){
        Log.d(TAG_LOG, "Get Query");
        Log.d(TAG_LOG, "Città: "+this.city_filter);
        Log.d(TAG_LOG, "Vote: "+this.vote_filter);
        Log.d(TAG_LOG, "Category: "+this.categories_filter);

        Query query1 = this.db.collection("restaurants");

        if (!this.city_filter.equals("")){
            Log.d(TAG_LOG, "Filtro per città");
            query1 = query1.whereEqualTo("city", this.city_filter);
        }

        if (this.vote_filter!=(new Float(0))){
            Log.d(TAG_LOG, "Filtro per voto");
            query1 = query1.whereGreaterThanOrEqualTo("vote", this.vote_filter);
        }

        if (this.categories_filter.size()!=0){
            Log.d(TAG_LOG, "Filtro per categoria");
            query1 = query1.whereIn("category", this.categories_filter);
        }

        Query query2 = this.db.collection("restaurants").whereEqualTo("city", "Brescia").whereGreaterThanOrEqualTo("vote", 4);
        return query1;
    }

    private int removeRestaurantFromData(String id){
        int index = 0;
        for (Restaurant n:this.mdata){

            if (n.getId().equals(id)){
                this.mdata.remove(n);
                return index;
            }
            index ++;
        }
        return index;
    }

    //TODO aggiungendo una recensione partirà un metood che aggiornerà numero di recensionie  voto sul ristorante
    private Restaurant createRestaurant(QueryDocumentSnapshot d){
        Log.d(TAG_LOG, "Creando un ristorante: "+(String)d.get("name"));
        Restaurant n = new Restaurant();
        n.setId(d.getId());
        n.setName((String)d.get("name"));
        n.setAddress((String)d.get("address"));
        n.setCity((String)d.get("city"));
        n.setCategory((String)d.get("category"));
        n.setImageUrl((String)d.get("imageUrl"));
        n.setPhone((String)d.get("phone"));
        n.setTags((List<String>) d.get("tags"));

        Double valore = (Double) d.get("vote");
        n.setVote(new Float(valore.floatValue()));

        Log.d(TAG_LOG, "Voto settato: "+valore);

        int in2 = d.getLong("n_reviews").intValue();
        n.setN_reviews((int)in2);






        Log.d(TAG_LOG, "Ristorante letto correttamente");
        return n;
    }


    private void createAdapter(ArrayList<Restaurant> data){
        if (this.admin)
            this.adapter = new RecyclerViewAdapter_Restaurant(getActivity(), data, Fragment_Restaurants_List_Client.this, true);
        else
            this.adapter = new RecyclerViewAdapter_Restaurant(getActivity(), data, Fragment_Restaurants_List_Client.this, false);
    }


    private void setSearchView(){
        //al cambio di orientazione la searchview viene resettata

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
                    getRestaurants();
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
                    getRestaurants();
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

    private void filter(String text) {
        Log.d(TAG_LOG,"filter");
        //this.listener_notification.remove();
        ArrayList<Restaurant> filteredList = new ArrayList<>();


        for (Restaurant item : this.mdata) {

            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
            if (filteredList.isEmpty()) {
                //Toast.makeText(getActivity(), "No data found...", Toast.LENGTH_SHORT).show();
                //Log.d(TAG_LOG,"filter list empty: "+filteredList.size());
            }
            else {
                //Log.d(TAG_LOG,"filter list not empty: "+filteredList.size());
            }

            createAdapter(filteredList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rv.setLayoutManager(linearLayoutManager);
            rv.setAdapter(adapter);

            setAdapterChange();
        }
    }

    private void setAdapterChange(){
        Log.d(TAG_LOG,"set Adapter");

        this.adapter.notifyDataSetChanged();
        if(adapter.getItemCount()==0)
            this.textView_no_result.setVisibility(View.VISIBLE);
        else
            this.textView_no_result.setVisibility(View.INVISIBLE);

    }



    //Interfaccia
    public interface RestaurantListInterfaceClient {
        public void goBack();
    }

    private Fragment_Restaurants_List_Client.RestaurantListInterfaceClient listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Restaurants_List_Client.RestaurantListInterfaceClient){
            listener = (Fragment_Restaurants_List_Client.RestaurantListInterfaceClient) activity;
        }
        else {
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }
}