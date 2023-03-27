package com.reodinas2.eatopiaapp.adapter;

import android.content.Context;
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
import com.reodinas2.eatopiaapp.model.Menu;
import com.reodinas2.eatopiaapp.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    Context context;
    ArrayList<Menu> MenuArrayList;

    public MenuAdapter(Context context, ArrayList<Menu> menuArrayList) {
        this.context = context;
        MenuArrayList = menuArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Menu menu = MenuArrayList.get(position);

        holder.txtMenuName .setText(menu.getMenuName());
        if (menu.getDescription() == null){
            holder.txtDescription .setText("");
        }else {
            holder.txtDescription .setText(menu.getDescription());
        }

        if (menu.getPrice() == -1){
            holder.txtPrice .setText("가격정보 없음");
        }else {
            holder.txtPrice .setText(menu.getPrice() + "원");
        }

        String imgUrl = menu.getImgUrl();
        Glide.with(context)
                .load(imgUrl)
                .placeholder(R.drawable.baseline_image_24)
                .centerCrop()
                .error(R.drawable.no_image)
                .into(holder.imgMenu);
    }

    @Override
    public int getItemCount() {
        return MenuArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imgMenu;
        TextView txtMenuName;
        TextView txtDescription;
        TextView txtCount;
        TextView txtPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            txtMenuName = itemView.findViewById(R.id.txtMenuName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtCount = itemView.findViewById(R.id.txtCount);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}
