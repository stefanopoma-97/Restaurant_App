package com.poma.restaurant.restaurant;

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
import com.poma.restaurant.model.Favourite;
import com.poma.restaurant.model.RecyclerViewAdapter.RecyclerViewAdapter_Favourite;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Favourite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Favourite extends Fragment {

    private static final String TAG_LOG = Fragment_Favourite.class.getName();
    private RecyclerView rv;
    private ArrayList<Favourite> mdata;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private static ListenerRegistration listener_notification;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerViewAdapter_Favourite adapter;
    private SearchView searchView = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Favourite() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Favourite.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Favourite newInstance(String param1, String param2) {
        Fragment_Favourite fragment = new Fragment_Favourite();
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
        View view= inflater.inflate(R.layout.fragment_favourite, container, false);

        //RV
        this.rv = view.findViewById(R.id.RV_fragment_favourite);


        this.searchView = view.findViewById(R.id.search_view_fragment_favourite);

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

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG_LOG,"on start");

        this.db = FirebaseFirestore.getInstance();
        this.mAuth= FirebaseAuth.getInstance();

        this.currentUser = mAuth.getCurrentUser();

        this.mdata = new ArrayList<>();

        getRestaurants();

        setSearchView();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG_LOG,"un register receiver");
        this.listener_notification.remove();
    }

    private void getRestaurants(){
        Log.d(TAG_LOG,"Get restaurants... quali?");

        getFavourites();

    }

    private void getFavourites(){
        Log.d(TAG_LOG,"get all restaurants");
        this.mdata = new ArrayList<>();

        createAdapter(mdata);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);

        if (this.currentUser!=null){
            Query query = this.db.collection("favourites").whereEqualTo("user_id",this.mAuth.getCurrentUser().getUid());

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
                                mdata.add(createFavourite(dc.getDocument()));
                                setAdapterChange();

                                break;
                            case MODIFIED:
                                Log.d(TAG_LOG, "modify notify: " + dc.getDocument().getData());
                                int pos = removeRestaurantFromData(dc.getDocument().getId());
                                mdata.add(pos, createFavourite(dc.getDocument()));
                                setAdapterChange();
                                break;
                            case REMOVED:
                                Log.d(TAG_LOG, "Removed notify (id = "+dc.getDocument().getId()+"): " + dc.getDocument().getData());
                                removeRestaurantFromData(dc.getDocument().getId());
                                setAdapterChange();
                                break;
                        }
                    }


                }
            });
        }
    }

    private int removeRestaurantFromData(String id){
        int index = 0;
        for (Favourite n:this.mdata){

            if (n.getId().equals(id)){
                this.mdata.remove(n);
                return index;
            }
            index ++;
        }
        return index;
    }

    private Favourite createFavourite(QueryDocumentSnapshot d){
        Log.d(TAG_LOG, "Creando un ristorante: "+(String)d.get("restaurant_name"));
        Favourite n = new Favourite();
        n.setId(d.getId());
        n.setRestaurant_name((String)d.get("restaurant_name"));
        n.setRestaurant_category((String)d.get("restaurant_category"));
        n.setRestaurant_address((String)d.get("restaurant_address"));
        n.setRestaurant_id((String)d.get("restaurant_id"));


        Log.d(TAG_LOG, "Ristorante letto correttamente");
        return n;
    }

    private void createAdapter(ArrayList<Favourite> data){
        this.adapter = new RecyclerViewAdapter_Favourite(getActivity(), data, Fragment_Favourite.this);

    }

    private void setSearchView(){
        //al cambio di orientazione la searchview viene resettata
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);
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
    }

    private void filter(String text) {
        Log.d(TAG_LOG,"filter");
        this.listener_notification.remove();
        ArrayList<Favourite> filteredList = new ArrayList<>();


        for (Favourite item : this.mdata) {

            if (item.getRestaurant_name().toLowerCase().contains(text.toLowerCase())) {
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

    }

    //Interfaccia
    public interface FavouriteListInterfaceClient {
        public void goBack();
    }

    private Fragment_Favourite.FavouriteListInterfaceClient listener;

    //check if activity implement the interface
    @Override
    public void onAttach(Activity activity){
        Log.d(TAG_LOG,"onAttach fragment");

        super.onAttach(activity);
        if(activity instanceof Fragment_Favourite.FavouriteListInterfaceClient){
            listener = (Fragment_Favourite.FavouriteListInterfaceClient) activity;
        }
        else {
            throw new ClassCastException(activity.toString() +
                    "Does not implement the interface");
        }
    }



}