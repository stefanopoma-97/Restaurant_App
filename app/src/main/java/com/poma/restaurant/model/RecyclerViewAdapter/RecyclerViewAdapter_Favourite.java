package com.poma.restaurant.model.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Favourite;

import java.util.List;


public class RecyclerViewAdapter_Favourite extends RecyclerView.Adapter<RecyclerViewAdapter_Favourite.MyViewHolder> {
    private static final String TAG_LOG = RecyclerViewAdapter_Favourite.class.getName();


    private Context mContext;
    private Fragment mFragment;
    private ViewGroup parent;
    private List<Favourite> mData;

    ///DB
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //tipologie
    private static int RISTORANTE_NORMALE = 1;
    private static int RISTORANTE_PREFERITO = 0;


    //listener per notifica
    private OnFavouriteClickListener onFavouriteClickListener;

    //costruttore
    public RecyclerViewAdapter_Favourite(Context context, List<Favourite> mData){
        this.mContext = context;
        this.mData = mData;
    }

    //costruttore
    public RecyclerViewAdapter_Favourite(Context context, List<Favourite> mData, Fragment mFragment){
        this.mContext = context;
        this.mData = mData;
        this.mFragment = mFragment;
        this.onFavouriteClickListener = (OnFavouriteClickListener) context;
    }


    @NonNull
    @Override
    public RecyclerViewAdapter_Favourite.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        this.mAuth= FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.parent = parent;

        view = mInflater.inflate(R.layout.card_restaurant_favourite, parent,false);

        return new RecyclerViewAdapter_Favourite.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_Favourite.MyViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        Favourite n = this.mData.get(position);


        holder.textview_card_favourite_title.setText(n.getRestaurant_name());
        holder.textview_card_favourite_category.setText(n.getRestaurant_category());
        holder.textview_card_favourite_adress.setText(n.getRestaurant_address());


        //Click sulla card
        holder.cardView_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavouriteClickListener.onRestaurantClick(n);

            }
        });



    }

    //Numero elementi
    @Override
    public int getItemCount() {
        return this.mData.size();

    }




    //Holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textview_card_favourite_title, textview_card_favourite_category, textview_card_favourite_adress;
        CardView cardView_favourite;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView_favourite = (CardView) itemView.findViewById(R.id.cardview_favourite);

            textview_card_favourite_title = (TextView) itemView.findViewById(R.id.textview_card_favourite_title);
            textview_card_favourite_category = (TextView) itemView.findViewById(R.id.textview_card_favourite_category);
            textview_card_favourite_adress = (TextView) itemView.findViewById(R.id.textview_card_favourite_address);



        }
    }


}
