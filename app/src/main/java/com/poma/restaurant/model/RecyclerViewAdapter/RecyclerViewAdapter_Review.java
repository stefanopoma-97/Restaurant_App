package com.poma.restaurant.model.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Favourite;
import com.poma.restaurant.model.Review;

import java.util.List;


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
        holder.load_more.setVisibility(View.VISIBLE);
    }

    private void visible(RecyclerViewAdapter_Review.MyViewHolder holder){
        holder.textview_location.setVisibility(View.VISIBLE);
        holder.textview_service.setVisibility(View.VISIBLE);
        holder.textview_problem.setVisibility(View.VISIBLE);
        holder.textview_experience.setVisibility(View.VISIBLE);
        holder.load_less.setVisibility(View.VISIBLE);
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



        }
    }


}
