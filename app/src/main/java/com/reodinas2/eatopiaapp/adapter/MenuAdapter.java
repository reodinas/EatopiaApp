package com.reodinas2.eatopiaapp.adapter;

import android.content.Context;
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
import com.reodinas2.eatopiaapp.model.Menu;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    Context context;
    ArrayList<Menu> menuArrayList;

    // 선택받은 메뉴를 저장할 새로운 어레이리스트
    ArrayList<Menu> selectedMenuList;


    public MenuAdapter(Context context, ArrayList<Menu> menuArrayList) {
        this.context = context;
        this.menuArrayList = menuArrayList;
        this.selectedMenuList = new ArrayList<>(); // selectedMenuList 초기화
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

        Menu menu = menuArrayList.get(position);

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

        holder.imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(holder.txtCount.getText().toString());
                count+=1;
                holder.txtCount.setText(String.valueOf(count));

                updateSelectedMenuList(menu, count);

                Log.i("updateSelectedMenuList", "menuName: " + selectedMenuList.get(selectedMenuList.size()-1).getMenuName());
                Log.i("updateSelectedMenuList", "count: " + selectedMenuList.get(selectedMenuList.size()-1).getCount());
            }
        });

        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(holder.txtCount.getText().toString());
                if (count > 0) {
                    count-=1;
                    holder.txtCount.setText(String.valueOf(count));

                    updateSelectedMenuList(menu, count);

                    Log.i("updateSelectedMenuList", "menuName: " + selectedMenuList.get(selectedMenuList.size()-1).getMenuName());
                    Log.i("updateSelectedMenuList", "count: " + selectedMenuList.get(selectedMenuList.size()-1).getCount());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuArrayList.size();
    }


    private void updateSelectedMenuList(Menu menu, int count) {
        boolean itemExists = false;

        for (Menu selectedMenu : selectedMenuList) {
            if (selectedMenu.getId() == menu.getId()) {
                if (count == 0) {
                    selectedMenuList.remove(selectedMenu);
                } else {
                    selectedMenu.setCount(count);
                }
                itemExists = true;
                break;
            }
        }

        if (!itemExists && count > 0) {
            Menu selectedMenu = new Menu();
            selectedMenu.setId(menu.getId());
            selectedMenu.setRestaurantId(menu.getRestaurantId());
            selectedMenu.setPrice(menu.getPrice());
            selectedMenu.setCount(count);
            selectedMenu.setMenuName(menu.getMenuName());
            selectedMenu.setDescription(menu.getDescription());
            selectedMenu.setImgUrl(menu.getImgUrl());
            selectedMenuList.add(selectedMenu);
        }
    }

    public ArrayList<Menu> getSelectedMenuList() {
        return selectedMenuList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imgMenu;
        TextView txtMenuName;
        TextView txtDescription;
        TextView txtCount;
        TextView txtPrice;
        ImageView imgMinus;
        ImageView imgPlus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            txtMenuName = itemView.findViewById(R.id.txtMenuName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtCount = itemView.findViewById(R.id.txtPeople);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgMinus = itemView.findViewById(R.id.imgMinus);
            imgPlus = itemView.findViewById(R.id.imgPlus);
        }
    }
}
