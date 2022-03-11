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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecyclerViewAdapter_Notification extends RecyclerView.Adapter<RecyclerViewAdapter_Notification.MyViewHolder> {
    private static final String TAG_LOG = RecyclerViewAdapter_Notification.class.getName();

    private static final String NOTIFICATION_ID_EXTRA = "com.poma.restaurant.NOTIFICATION_ID_EXTRA";


    private Context mContext;
    private Fragment mFragment;
    private ViewGroup parent;
    private List<Notification> mData;

    ///DB
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User currentUser2;
    private Uri imageUri;

    private static int NOTIFICA_NON_LETTA = 1;
    private static int NOTIFICA_LETTA = 0;

    //listener per notifica
    private OnNotificationClickListener onNotificationClickListener;

    //costruttore
    public RecyclerViewAdapter_Notification (Context context, List<Notification> mData){
        this.mContext = context;
        this.mData = mData;
    }

    //costruttore
    public RecyclerViewAdapter_Notification (Context context, List<Notification> mData, Fragment mFragment){
        this.mContext = context;
        this.mData = mData;
        this.mFragment = mFragment;

        this.onNotificationClickListener = (OnNotificationClickListener) context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_Notification.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        this.mAuth= FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.parent = parent;

        if (viewType == NOTIFICA_LETTA)
            view = mInflater.inflate(R.layout.card_notification_normal, parent,false);
        else
            view = mInflater.inflate(R.layout.card_notification_new, parent,false);

        return new RecyclerViewAdapter_Notification.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_Notification.MyViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        Notification n = this.mData.get(position);


        holder.textview_notification_title.setText(n.getType());
        holder.textview_notification_description.setText(n.getContent());
        holder.textview_notification_date.setText(n.getDateformatter());
        //holder.progressBar.setVisibility(View.VISIBLE);


        //holder.imageView_icon_new_notification.setVisibility(View.VISIBLE);

        holder.cardView_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNotificationClickListener.onNotificationClick(n);
                /*
                Log.d(TAG_LOG, "Click su notifica, start activity");
                final Intent intent = new Intent(v.getContext(), Activity_Notification.class);
                intent.putExtra(NOTIFICATION_ID_EXTRA, n.getId());
                v.getContext().startActivity(intent);
                */


            }
        });

        holder.imageView_icon_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItemViewType(position)==NOTIFICA_LETTA)
                    setRead(n);
                else
                    setNotRead(n);

            }
        });

       /* if (getItemViewType(position) == NOTIFICA_NON_LETTA)
            updateImageView(holder, n);*/

    }

    //Numero elementi
    @Override
    public int getItemCount() {
        return this.mData.size();

    }

    @Override
    public int getItemViewType(int position) {
        Notification n = this.mData.get(position);
        if (n.getRead()){
            return NOTIFICA_LETTA;
        }
        else {
            return NOTIFICA_NON_LETTA;
        }

    }

    //Holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textview_notification_title, textview_notification_description, textview_notification_date;
        CardView cardView_notification;
        ImageView imageView_icon_notification;
        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView_notification = (CardView) itemView.findViewById(R.id.cardview_restaurant);

            imageView_icon_notification = (ImageView) itemView.findViewById(R.id.icon_card_restaurant);
            //imageView_icon_new_notification = (ImageView) itemView.findViewById(R.id.icon_new_notification);

            textview_notification_title = (TextView) itemView.findViewById(R.id.textview_card_restaurant_title);
            textview_notification_description = (TextView) itemView.findViewById(R.id.textview_card_restaurant_adress);
            textview_notification_date = (TextView) itemView.findViewById(R.id.textview_card_restaurant_tag1);

            progressBar = (ProgressBar)itemView.findViewById(R.id.progress_bar_card_restaurant);

        }
    }

    private void setRead(Notification n){
        Map<String, Object> updates = new HashMap<>();
        Log.d(TAG_LOG, "notifica get Read: "+n.getRead());
        updates.put("read", true);



        DocumentReference document = db.collection("notifications").document(n.getId());
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

    private void setNotRead(Notification n){
        Map<String, Object> updates = new HashMap<>();
        updates.put("read", false);

        DocumentReference document = db.collection("notifications").document(n.getId());
        document.set(updates, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG_LOG, "aggiorno notifica -> read: false");

                        }
                        else{
                            Log.d(TAG_LOG, "problemi aggiornamento notifica");

                        }
                    }
                });

    }

    //Caricamento asincrono imageview
    public void updateImageView(RecyclerViewAdapter_Notification.MyViewHolder holder, Notification n) {

            Log.d(TAG_LOG, "inizio update imageView");
            holder.progressBar.setVisibility(View.VISIBLE);

            DocumentReference docRef = db.collection("users").document("EzgFTtDJASXvESC9FZunuJ6uFFQ2");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG_LOG, "task successfull");
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> data = document.getData();
                            Log.d(TAG_LOG, "DocumentSnapshot data: " + data);

                            if ((String)data.get("imageUrl") != null){
                                Uri uri = Uri.parse((String)data.get("imageUrl"));
                                Log.d("firebase", "Image Url: " + uri);
                                Glide.with(mContext)
                                        .load(uri)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                holder.progressBar.setVisibility(View.INVISIBLE);
                                                return false;
                                            }
                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                Log.d(TAG_LOG, "Glide on resource ready");
                                                holder.progressBar.setVisibility(View.INVISIBLE);
                                                return false;
                                            }
                                        })
                                        .into(holder.imageView_icon_notification);
                            }
                            //progressBar.setVisibility(View.INVISIBLE);



                        } else {
                            Log.d(TAG_LOG, "No such document");
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            //progressDialog(false,"");
                            //progressBar.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Log.d(TAG_LOG, "get failed with ", task.getException());
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        //progressDialog(false,"");
                        //progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });

    }
}
