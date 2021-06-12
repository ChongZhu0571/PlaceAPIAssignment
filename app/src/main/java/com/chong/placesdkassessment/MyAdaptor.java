package com.chong.placesdkassessment;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.ViewHolder> {

    List<String> names;
    List<String> status;
    List<String> addresses;
    List<Bitmap> images;

    public MyAdaptor(List<String> names, List<String> status,List<String> addresses) {
        this.names = names;
        this.status = status;
        this.addresses = addresses;
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
     //   holder.img.setImageBitmap(images.get(position));
        holder.txt_address.setText(addresses.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView txt_name;
        TextView txt_status;
        TextView txt_address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            txt_name = itemView.findViewById(R.id.name);
            txt_status = itemView.findViewById(R.id.status);
            txt_address = itemView.findViewById(R.id.address);
        }
    }
}
