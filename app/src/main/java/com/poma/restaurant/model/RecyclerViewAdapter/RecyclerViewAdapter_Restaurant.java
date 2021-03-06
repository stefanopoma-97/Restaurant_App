package com.poma.restaurant.model.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Favourite;
import com.poma.restaurant.model.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RecyclerViewAdapter_Restaurant extends RecyclerView.Adapter<RecyclerViewAdapter_Restaurant.MyViewHolder> {
    private static final String TAG_LOG = RecyclerViewAdapter_Restaurant.class.getName();


    private Context mContext;
    private Fragment mFragment;
    private ViewGroup parent;
    private List<Restaurant> mData;
    private List<Favourite> mData2;
    private Boolean favourite = false;
    private Boolean admin_access = false;

    ///DB
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //tipologie
    private static int RISTORANTE_NORMALE = 1;
    private static int RISTORANTE_PREFERITO = 0;


    //listener per notifica
    private OnRestaurantClickListener onRestaurantClickListener;

    //costruttore
    public RecyclerViewAdapter_Restaurant(Context context, List<Restaurant> mData){
        this.mContext = context;
        this.mData = mData;
    }

    //costruttore
    public RecyclerViewAdapter_Restaurant(Context context, List<Restaurant> mData, Fragment mFragment){
        this.mContext = context;
        this.mData = mData;
        this.mFragment = mFragment;
        this.onRestaurantClickListener = (OnRestaurantClickListener) context;
    }



    public RecyclerViewAdapter_Restaurant(Context context, List<Restaurant> mData, Fragment mFragment, Boolean admin){
        this.mContext = context;
        this.mData = mData;
        this.mFragment = mFragment;
        this.onRestaurantClickListener = (OnRestaurantClickListener) context;
        this.admin_access = admin;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_Restaurant.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        this.mAuth= FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.parent = parent;

        if (viewType == RISTORANTE_NORMALE)
            view = mInflater.inflate(R.layout.card_restaurant, parent,false);
        else
            view = mInflater.inflate(R.layout.card_restaurant_favourite, parent,false);

        return new RecyclerViewAdapter_Restaurant.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_Restaurant.MyViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        Restaurant n = this.mData.get(position);

        holder.textview_card_restaurant_title.setText(n.getName());
        holder.textview_card_restaurant_category.setText(n.getCategory());
        holder.textview_card_restaurant_adress.setText(n.getAddress()+" - "+n.getCity());
        holder.textview_card_restaurant_review.setText(n.getN_reviews() +" Reviews");

        ArrayList<String> tags = (ArrayList<String>)n.getTags();
        String tag = "";
        for (String s : tags){
            tag = tag+s+" ";
        }
        holder.textview_card_restaurant_tag1.setText(tag);

        //Click sulla card
        holder.cardView_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestaurantClickListener.onRestaurantClick(n);

            }
        });


        if (getItemViewType(position)==RISTORANTE_NORMALE){
            //carico immagine
            updateImageView(holder, n);

            //Stelle
            populated_star(holder, n);

            if(this.admin_access){
                holder.view.setVisibility(View.GONE);
                holder.btn_direction.setVisibility(View.GONE);
                holder.btn_call.setVisibility(View.GONE);
            }
            else {


            }

            Log.d(TAG_LOG, "inizio update imageView");
            boolean open = n.isOpen();
            if (open){
                holder.textView_open.setVisibility(View.VISIBLE);
                holder.textView_close.setVisibility(View.INVISIBLE);
            }
            else{
                holder.textView_open.setVisibility(View.INVISIBLE);
                holder.textView_close.setVisibility(View.VISIBLE);
            }

            holder.btn_direction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    direction(n);

                }
            });

            holder.btn_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call(n);
                }
            });
        }


    }

    //Numero elementi
    @Override
    public int getItemCount() {
        return this.mData.size();

    }

    @Override
    public int getItemViewType(int position) {
        if(this.favourite)
            return RISTORANTE_PREFERITO;
        else
            return RISTORANTE_NORMALE;

    }



    //Holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textview_card_restaurant_title, textview_card_restaurant_category, textview_card_restaurant_adress,
                textview_card_restaurant_tag1,
                textview_card_restaurant_review,
                textView_open, textView_close;
        CardView cardView_restaurant;
        ImageView imageView_icon_restaurant, star1, star2, star3, star4, star5;
        Button btn_direction, btn_call;
        ProgressBar progressBar;
        View view;

        AppCompatRatingBar rating;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView_restaurant = (CardView) itemView.findViewById(R.id.cardview_favourite);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar_card_favourite);

            imageView_icon_restaurant = (ImageView) itemView.findViewById(R.id.icon_card_favourite);
            /*star1 = (ImageView) itemView.findViewById(R.id.star1_card_restaurant);
            star2 = (ImageView) itemView.findViewById(R.id.star2_card_restaurant);
            star3 = (ImageView) itemView.findViewById(R.id.star3_card_restaurant);
            star4 = (ImageView) itemView.findViewById(R.id.star4_card_restaurant);
            star5 = (ImageView) itemView.findViewById(R.id.star5_card_restaurant);
            view1_cardreview

             */
            view = (View) itemView.findViewById(R.id.view1_card_restaurant);
            btn_call = (Button)itemView.findViewById(R.id.btn_card_restaurant_call);
            btn_direction = (Button)itemView.findViewById(R.id.btn_card_restaurant_map);

            rating = (AppCompatRatingBar) itemView.findViewById(R.id.rating_card_restaurant);

            textview_card_restaurant_title = (TextView) itemView.findViewById(R.id.textview_card_favourite_title);
            textview_card_restaurant_category = (TextView) itemView.findViewById(R.id.textview_card_favourite_category);
            textview_card_restaurant_adress = (TextView) itemView.findViewById(R.id.textview_card_favourite_adress);
            textview_card_restaurant_tag1 = (TextView) itemView.findViewById(R.id.textview_card_restaurant_tag1);
            textview_card_restaurant_review = (TextView) itemView.findViewById(R.id.textview_card_restaurant_number_review);
            textView_open = (TextView) itemView.findViewById(R.id.textview_card_open);
            textView_close = (TextView) itemView.findViewById(R.id.textview_card_close);


        }
    }


    //Caricamento asincrono imageview
    public void updateImageView(RecyclerViewAdapter_Restaurant.MyViewHolder holder, Restaurant n) {

            Log.d(TAG_LOG, "inizio update imageView");
            holder.progressBar.setVisibility(View.VISIBLE);

            DocumentReference docRef = db.collection("restaurants").document(n.getId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG_LOG, "task successfull");
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
                            Log.d(TAG_LOG, "DocumentSnapshot data: " + data);

                            if ((String)data.get("imageUrl") != null && (String)data.get("imageUrl")!= ""){
                                Uri uri = Uri.parse((String)data.get("imageUrl"));
                                Log.d("firebase", "Image Url: " + uri);
                                Glide.with(mContext)
                                        .load(uri)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                holder.progressBar.setVisibility(View.INVISIBLE);
                                                holder.imageView_icon_restaurant.setImageResource(R.drawable.ic_baseline_image_24);
                                                return false;
                                            }
                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                Log.d(TAG_LOG, "Glide on resource ready");
                                                holder.progressBar.setVisibility(View.INVISIBLE);
                                                return false;
                                            }
                                        })
                                        .into(holder.imageView_icon_restaurant);
                            }
                            else {
                                holder.progressBar.setVisibility(View.INVISIBLE);
                                holder.imageView_icon_restaurant.setImageResource(R.drawable.ic_baseline_image_24);
                            }



                        } else {
                            Log.d(TAG_LOG, "No such document");
                            holder.progressBar.setVisibility(View.INVISIBLE);

                        }
                    } else {
                        Log.d(TAG_LOG, "get failed with ", task.getException());
                        holder.progressBar.setVisibility(View.INVISIBLE);

                    }
                }
            });

    }

    private void direction(Restaurant r){
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?daddr="+r.getAddress()+" "+r.getCity()));
        this.mContext.startActivity(intent);
    }

    private void call(Restaurant r){
        String number = r.getPhone();
        Uri num = Uri.parse("tel:" + number);
        Intent dial = new Intent(Intent.ACTION_DIAL);
        dial.setData(num);
        this.mContext.startActivity(dial);

    }

    private void populated_star(RecyclerViewAdapter_Restaurant.MyViewHolder holder, Restaurant r){

        holder.rating.setRating(r.getVote());

        Log.d(TAG_LOG, "Populated stars");

        /*
        Log.d(TAG_LOG, "voto ristorante: "+r.getName()+" Voto:"+r.getVote());
        int stelle = (int) Math.round(r.getVote());
        Log.d(TAG_LOG, "Roud: "+stelle);
        if (stelle==4){
            holder.star5.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star5.setColorFilter(R.color.grey);
        }
        if (stelle==3){
            holder.star5.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star4.setImageResource(R.drawable.ic_baseline_star_border_24);

            holder.star5.setColorFilter(R.color.grey);
            holder.star4.setColorFilter(R.color.grey);
        }
        if (stelle==2){
            holder.star5.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star4.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star3.setImageResource(R.drawable.ic_baseline_star_border_24);

            holder.star5.setColorFilter(R.color.grey);
            holder.star4.setColorFilter(R.color.grey);
            holder.star3.setColorFilter(R.color.grey);
        }
        if (stelle==1){
            holder.star5.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star4.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star3.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star2.setImageResource(R.drawable.ic_baseline_star_border_24);

            holder.star5.setColorFilter(R.color.grey);
            holder.star4.setColorFilter(R.color.grey);
            holder.star3.setColorFilter(R.color.grey);
            holder.star2.setColorFilter(R.color.grey);

        }
        if (stelle==0){
            holder.star5.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star4.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star3.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star2.setImageResource(R.drawable.ic_baseline_star_border_24);
            holder.star1.setImageResource(R.drawable.ic_baseline_star_border_24);

            holder.star5.setColorFilter(R.color.grey);
            holder.star4.setColorFilter(R.color.grey);
            holder.star3.setColorFilter(R.color.grey);
            holder.star2.setColorFilter(R.color.grey);
            holder.star1.setColorFilter(R.color.grey);

        }

         */
    }
}
