package com.poma.restaurant.review;

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
import com.poma.restaurant.model.RecyclerViewAdapter.RecyclerViewAdapter_Review;
import com.poma.restaurant.model.Review;
import com.poma.restaurant.restaurant.Fragment_Favourites;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Reviews_List#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Reviews_List extends Fragment {

    private static final String TAG_LOG = Fragment_Reviews_List.class.getName();

    private RecyclerView rv;
    private ArrayList<Review> mdata;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private static ListenerRegistration listener_notification;

    private String restaurant_id;

    private RecyclerViewAdapter_Review adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Reviews_List() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Reviews_List.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Reviews_List newInstance(String param1, String param2) {
        Fragment_Reviews_List fragment = new Fragment_Reviews_List();
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
        View view= inflater.inflate(R.layout.fragment_reviews_list, container, false);

        //RV
        this.rv = view.findViewById(R.id.RV_fragment_review);


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

        getReviews();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG_LOG,"un register receiver");
        this.listener_notification.remove();
    }

    private void getReviews(){
        Log.d(TAG_LOG,"get all restaurants");
        this.mdata = new ArrayList<>();

        createAdapter(mdata);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);

        if (this.currentUser!=null){
            Query query = this.db.collection("reviews").whereEqualTo("restaurant_id",this.restaurant_id);

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
                                mdata.add(createReview(dc.getDocument()));
                                setAdapterChange();

                                break;
                            case MODIFIED:
                                Log.d(TAG_LOG, "modify notify: " + dc.getDocument().getData());
                                int pos = removeReviewFromData(dc.getDocument().getId());
                                mdata.add(pos, createReview(dc.getDocument()));
                                setAdapterChange();
                                break;
                            case REMOVED:
                                Log.d(TAG_LOG, "Removed notify (id = "+dc.getDocument().getId()+"): " + dc.getDocument().getData());
                                removeReviewFromData(dc.getDocument().getId());
                                setAdapterChange();
                                break;
                        }
                    }


                }
            });
        }
    }

    private int removeReviewFromData(String id){
        int index = 0;
        for (Review n:this.mdata){

            if (n.getId().equals(id)){
                this.mdata.remove(n);
                return index;
            }
            index ++;
        }
        return index;
    }

    private Review createReview(QueryDocumentSnapshot d){
        Log.d(TAG_LOG, "Creando una recensione");
        Review n = new Review();
        n.setId(d.getId());
        n.setUsername((String)d.get("username"));
        n.setRestaurant_id((String)d.get("restaurant_id"));
        n.setUser_id((String)d.get("user_id"));
        n.setLocation((String)d.get("location"));
        n.setService((String)d.get("service"));
        n.setExperience((String)d.get("experience"));
        n.setProblems((String)d.get("problem"));

        Double valore = (Double) d.get("vote");
        n.setVote(new Float(valore.floatValue()));


        Log.d(TAG_LOG, "Ristorante letto correttamente");
        return n;
    }

    private void setAdapterChange(){
        Log.d(TAG_LOG,"set Adapter");

        this.adapter.notifyDataSetChanged();

    }

    private void createAdapter(ArrayList<Review> data){
        this.adapter = new RecyclerViewAdapter_Review(getActivity(), data, Fragment_Reviews_List.this);

    }

    public void setRestaurant_id(String id){
        this.restaurant_id=id;
    }
}