package com.poma.restaurant.model.RecyclerViewAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.poma.restaurant.model.Review;

import java.util.List;
import java.util.Map;


public class RecyclerViewAdapter_Review extends RecyclerView.Adapter<RecyclerViewAdapter_Review.MyViewHolder> {
    private static final String TAG_LOG = RecyclerViewAdapter_Review.class.getName();


    private Context mContext;
    private Fragment mFragment;
    private ViewGroup parent;
    private List<Review> mData;

    ///DB
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;



    //listener per notifica
    private OnReviewClickListener onReviewClickListener;

    //costruttore
    public RecyclerViewAdapter_Review(Context context, List<Review> mData){
        this.mContext = context;
        this.mData = mData;
    }

    //costruttore
    public RecyclerViewAdapter_Review(Context context, List<Review> mData, Fragment mFragment){
        this.mContext = context;
        this.mData = mData;
        this.mFragment = mFragment;
        //this.onReviewClickListener = (OnReviewClickListener) context;
    }


    @NonNull
    @Override
    public RecyclerViewAdapter_Review.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        this.mAuth= FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.parent = parent;

        view = mInflater.inflate(R.layout.card_review, parent,false);

        return new RecyclerViewAdapter_Review.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_Review.MyViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        Review n = this.mData.get(position);


        holder.username.setText(n.getUsername());
        holder.textview_location.setText(n.getLocation());
        holder.textview_service.setText(n.getService());
        holder.textview_problem.setText(n.getProblems());
        holder.textview_experience.setText(n.getExperience());
        holder.rating.setRating(n.getVote());

        invisible(holder);

        updateImageView(holder, n);


        //Click sulla card
        holder.cardView_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onReviewClickListener.onReviewClick(n);

            }
        });

        holder.load_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible(holder);

            }
        });
        holder.load_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invisible(holder);

            }
        });



    }

    private void invisible(RecyclerViewAdapter_Review.MyViewHolder holder){
        holder.textview_location.setVisibility(View.GONE);
        holder.textview_service.setVisibility(View.GONE);
        holder.textview_problem.setVisibility(View.GONE);
        holder.textview_experience.setVisibility(View.GONE);
        holder.load_less.setVisibility(View.GONE);

        holder.title_location.setVisibility(View.GONE);
        holder.title_service.setVisibility(View.GONE);
        holder.title_problem.setVisibility(View.GONE);
        holder.title_experience.setVisibility(View.GONE);

        holder.view1.setVisibility(View.GONE);
        holder.view2.setVisibility(View.GONE);
        holder.view3.setVisibility(View.GONE);
        holder.view4.setVisibility(View.GONE);

        holder.load_more.setVisibility(View.VISIBLE);
    }

    private void visible(RecyclerViewAdapter_Review.MyViewHolder holder){
        if (!holder.textview_location.getText().equals("")){
            holder.textview_location.setVisibility(View.VISIBLE);
            holder.title_location.setVisibility(View.VISIBLE);
            holder.view1.setVisibility(View.VISIBLE);
        }

        if (!holder.textview_service.getText().equals("")){
            holder.textview_service.setVisibility(View.VISIBLE);
            holder.title_service.setVisibility(View.VISIBLE);
            holder.view2.setVisibility(View.VISIBLE);
        }

        if (!holder.textview_experience.getText().equals("")){
            holder.textview_experience.setVisibility(View.VISIBLE);
            holder.title_experience.setVisibility(View.VISIBLE);

            holder.view3.setVisibility(View.VISIBLE);
        }

        if (!holder.textview_problem.getText().equals("")){
            holder.textview_problem.setVisibility(View.VISIBLE);
            holder.title_problem.setVisibility(View.VISIBLE);

        }



        holder.load_less.setVisibility(View.VISIBLE);

        holder.view4.setVisibility(View.GONE);
        holder.load_more.setVisibility(View.GONE);
    }

    //Numero elementi
    @Override
    public int getItemCount() {
        return this.mData.size();

    }




    //Holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textview_location, textview_service, textview_experience, textview_problem, username, load_more, load_less;
        CardView cardView_review;
        ImageView image;
        AppCompatRatingBar rating;
        ProgressBar progressBar;

        View view1, view2, view3, view4;
        TextView title_location, title_service, title_experience, title_problem;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView_review = (CardView) itemView.findViewById(R.id.cardview_review);

            textview_location = (TextView) itemView.findViewById(R.id.textview_location_content_cardreview);
            textview_service = (TextView) itemView.findViewById(R.id.textview_service_content_cardreview);
            textview_experience = (TextView) itemView.findViewById(R.id.textview_experience_content_cardreview);
            textview_problem = (TextView) itemView.findViewById(R.id.textview_problem_content_cardreview);
            username = (TextView) itemView.findViewById(R.id.textview_username_cardreview);
            load_more = (TextView) itemView.findViewById(R.id.textview_load_more_cardreview);
            load_less = (TextView) itemView.findViewById(R.id.textview_load_less_cardreview);

            image = (ImageView) itemView.findViewById(R.id.appCompatImageView_cardreview);

            rating = (AppCompatRatingBar) itemView.findViewById(R.id.ratingbar_cardreview);


            title_location = (TextView) itemView.findViewById(R.id.textview_location_cardreview);
            title_service = (TextView) itemView.findViewById(R.id.textview_service_cardreview);
            title_experience = (TextView) itemView.findViewById(R.id.textview_experience_cardreview);
            title_problem = (TextView) itemView.findViewById(R.id.textview_problem_cardreview);


            view1 = (View) itemView.findViewById(R.id.view1_cardreview);
            view2 = (View) itemView.findViewById(R.id.view2_cardreview);
            view3 = (View) itemView.findViewById(R.id.view3_cardreview);
            view4 = (View) itemView.findViewById(R.id.view4_cardreview);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar_cardreview);



        }
    }

    //Caricamento asincrono imageview
    public void updateImageView(RecyclerViewAdapter_Review.MyViewHolder holder, Review n) {

        Log.d(TAG_LOG, "inizio update imageView");
        holder.progressBar.setVisibility(View.VISIBLE);

        DocumentReference docRef = db.collection("users").document(n.getUser_id());
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
                                            holder.image.setImageResource(R.drawable.ic_baseline_image_24);
                                            return false;
                                        }
                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            Log.d(TAG_LOG, "Glide on resource ready");
                                            holder.progressBar.setVisibility(View.INVISIBLE);
                                            return false;
                                        }
                                    })
                                    .into(holder.image);
                        }
                        else {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            holder.image.setImageResource(R.drawable.ic_baseline_account_circle_24);
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



}
