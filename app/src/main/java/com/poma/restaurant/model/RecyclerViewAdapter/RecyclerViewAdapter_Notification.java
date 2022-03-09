package com.poma.restaurant.model.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.poma.restaurant.R;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.User;
import com.poma.restaurant.notifications.Activity_Notification;

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

    private static int NOTIFICA_NON_LETTA = 1;
    private static int NOTIFICA_LETTA = 0;

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

        //holder.imageView_icon_new_notification.setVisibility(View.VISIBLE);

        holder.cardView_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_LOG, "Click su notifica, start activity");
                final Intent intent = new Intent(v.getContext(), Activity_Notification.class);
                intent.putExtra(NOTIFICATION_ID_EXTRA, n.getId());
                v.getContext().startActivity(intent);

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
        ImageView imageView_icon_new_notification;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView_notification = (CardView) itemView.findViewById(R.id.cardview_notification);

            imageView_icon_notification = (ImageView) itemView.findViewById(R.id.icon_notification);
            imageView_icon_new_notification = (ImageView) itemView.findViewById(R.id.icon_new_notification);

            textview_notification_title = (TextView) itemView.findViewById(R.id.textview_single_notification_title);
            textview_notification_description = (TextView) itemView.findViewById(R.id.textview_notification_description);
            textview_notification_date = (TextView) itemView.findViewById(R.id.textview_notification_date);

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
}
