package com.chong.placesdkassessment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.List;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.ViewHolder> {

    List<String> names;
    List<String> status;
    List<String> addresses;
    List<String> images;
    List<String> placeIds;
    private AdapterCallback mAdapterCallback;
    Context context;
    public MyAdaptor(List<String> names,List<String> placeIds,List<String> images,List<String> status,List<String> addresses,Context context,AdapterCallback mAdapterCallback) {
        this.names = names;
        this.placeIds = placeIds;
        this.context = context;
        this.status = status;
       this.images = images;
        this.addresses = addresses;
        this.mAdapterCallback = mAdapterCallback;
    }

    @NonNull
    @Override
    public MyAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view =inflater.inflate(R.layout.myrecycleview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdaptor.ViewHolder holder, int position) {
        holder.txt_name.setText(names.get(position));
        holder.txt_status.setText(status.get(position));
        Picasso.get().load(images.get(position)).into(holder.img);
        holder.txt_address.setText(addresses.get(position));
        holder.detailClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallback.onMethodCallback(placeIds.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
    public static interface AdapterCallback {
        void onMethodCallback(String placeID);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView txt_name;
        TextView txt_status;
        TextView txt_address;
        ConstraintLayout detailClick;
        CardView cardView;
        Switch btn_switch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            txt_name = itemView.findViewById(R.id.name);
            txt_status = itemView.findViewById(R.id.status);
            txt_address = itemView.findViewById(R.id.address);
            detailClick = itemView.findViewById(R.id.detail_clicked);
            cardView = itemView.findViewById(R.id.view_cardView);
            btn_switch = itemView.findViewById(R.id.swh_outcome);
        }
    }
}
