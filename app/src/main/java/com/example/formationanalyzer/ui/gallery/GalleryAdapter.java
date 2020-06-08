package com.example.formationanalyzer.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.formationanalyzer.R;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    ArrayList<Integer> images;
    Context ctx;
    GalleryInterface item;
    Integer clicked_url;


    public GalleryAdapter(ArrayList<Integer> images, Context ctx, GalleryInterface item) {
        this.images = images;
        this.ctx = ctx;
        this.item = item;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_main, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int k = images.get(position);
        Glide.with(ctx).load(k).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;


        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            image = itemView.findViewById(R.id.image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick2(v);
                }
            });


        }

        public void onClick2(View view) {
            int position = getLayoutPosition();
            clicked_url = images.get(position);
            Toast.makeText(view.getContext(), "item is clicked", Toast.LENGTH_SHORT).show();
            if (item != null) {
                item.itemSelected(clicked_url);
            }
        }

    }

}