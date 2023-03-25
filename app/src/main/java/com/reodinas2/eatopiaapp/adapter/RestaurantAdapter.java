package com.reodinas2.eatopiaapp.adapter;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.reodinas2.eatopiaapp.R;
import com.reodinas2.eatopiaapp.RestaurantActivity;
import com.reodinas2.eatopiaapp.model.Restaurant;

import java.util.ArrayList;


public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    Context context;
    ArrayList<Restaurant> restaurantArrayList;


    public RestaurantAdapter(Context context, ArrayList<Restaurant> restaurantArrayList) {
        this.context = context;
        this.restaurantArrayList = restaurantArrayList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantArrayList.get(position);

        holder.txtName.setText(restaurant.getName());
        holder.txtDistance.setText(restaurant.getDistance()+"m");
        holder.txtCategory.setText(restaurant.getCategory());
        holder.txtAvg.setText(""+restaurant.getAvg());
        holder.txtCnt.setText(""+restaurant.getCnt());
        String address = restaurant.getLocCity()+" "+restaurant.getLocDistrict()+" "+restaurant.getLocDetail();
        holder.txtAddress.setText(address);

        String imgUrl = restaurant.getImgUrl();
        Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.baseline_image_24)
                    .centerCrop()
                    .error(R.drawable.baseline_image_24)
                    .into(holder.imgRestaurant);


    }

    @Override
    public int getItemCount() {
        return restaurantArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imgRestaurant;
        TextView txtName;
        TextView txtDistance;
        TextView txtCategory;
        TextView txtAvg;
        TextView txtCnt;
        TextView txtAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            txtName = itemView.findViewById(R.id.txtName);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtAvg = itemView.findViewById(R.id.txtAvg);
            txtCnt = itemView.findViewById(R.id.txtCnt);
            txtAddress = itemView.findViewById(R.id.txtAddress);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    Restaurant restaurant = restaurantArrayList.get(index);

                    Intent intent = new Intent(context, RestaurantActivity.class);
                    intent.putExtra("restaurant", restaurant);
                    context.startActivity(intent);

                }
            });

        }

    }

}