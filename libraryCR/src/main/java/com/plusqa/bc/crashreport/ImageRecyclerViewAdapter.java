package com.plusqa.bc.crashreport;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;

public class ImageRecyclerViewAdapter
        extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {

    private List<Bitmap> images;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    // data is passed into the constructor
    ImageRecyclerViewAdapter(Context context, List<Bitmap> images) {

        this.inflater = LayoutInflater.from(context);
        this.images = images;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Bitmap image = images.get(position);
        holder.imageView.setImageBitmap(image);

        holder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                images.remove(position);

                notifyDataSetChanged();

                notifyItemRemoved(position);


            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return images.size();
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton subtractButton;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rv_item_image);
            subtractButton = itemView.findViewById(R.id.subtract_button);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // convenience method for getting data at click position
    public Bitmap getItem(int id) {
        return images.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
