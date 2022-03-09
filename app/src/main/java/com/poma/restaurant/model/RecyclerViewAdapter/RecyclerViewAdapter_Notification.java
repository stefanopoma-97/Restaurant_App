package com.poma.restaurant.model.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.poma.restaurant.R;
import com.poma.restaurant.account.Activity_Account;
import com.poma.restaurant.model.Notification;
import com.poma.restaurant.model.User;
import com.poma.restaurant.utilities.MyApplication;

import java.util.List;


public class RecyclerViewAdapter_Notification extends RecyclerView.Adapter<RecyclerViewAdapter_Notification.MyViewHolder> {
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
        holder.textview_notification_date.setText(n.getDate());

        //holder.imageView_icon_new_notification.setVisibility(View.VISIBLE);

        holder.cardView_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Click notifica con id: "+n.getId(), Toast.LENGTH_SHORT).show();

            }
        });

        holder.imageView_icon_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Icona", Toast.LENGTH_SHORT).show();

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

            textview_notification_title = (TextView) itemView.findViewById(R.id.textview_notification_title);
            textview_notification_description = (TextView) itemView.findViewById(R.id.textview_notification_description);
            textview_notification_date = (TextView) itemView.findViewById(R.id.textview_notification_date);

        }
    }


}
