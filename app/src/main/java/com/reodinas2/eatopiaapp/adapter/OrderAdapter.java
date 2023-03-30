package com.reodinas2.eatopiaapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.reodinas2.eatopiaapp.R;
import com.reodinas2.eatopiaapp.model.Menu;
import com.reodinas2.eatopiaapp.model.MyOrder;
import com.reodinas2.eatopiaapp.model.Order;
import com.reodinas2.eatopiaapp.model.Restaurant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{

    Context context;
    ArrayList<MyOrder> myOrderArrayList;

    SimpleDateFormat sf;
    SimpleDateFormat df;

    @SuppressLint("SimpleDateFormat")
    public OrderAdapter(Context context, ArrayList<MyOrder> myOrderArrayList) {
        this.context = context;
        this.myOrderArrayList = myOrderArrayList;

        // UTC => Local Time
        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.setTimeZone(TimeZone.getDefault());
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_row, parent, false);
        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {

        MyOrder myOrder = myOrderArrayList.get(position);
        Order orderInfo = myOrder.getOrderInfo();
        Restaurant restaurantInfo = myOrder.getRestaurantInfo();
        List<Menu> menuInfo = myOrder.getMenuInfo();

        // 식당이름
        holder.txtName.setText(restaurantInfo.getName());

        // 식당이미지
        String imgUrl = restaurantInfo.getImgUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(context)
                    .load(imgUrl)
                    .placeholder(R.drawable.baseline_image_24)
                    .centerCrop()
                    .error(R.drawable.no_image)
                    .into(holder.imgRestaurant);
        } else {
            holder.imgRestaurant.setImageResource(R.drawable.no_image);
        }

        // 예약시간, 주문시간을 로컬시간으로 변경
        try {
            Date dateReservTime = sf.parse(orderInfo.getReservTime());
            Date dateCreatedAt = sf.parse(orderInfo.getCreatedAt());

            String localReservTime = df.format(dateReservTime);
            String localCreatedAt = df.format(dateCreatedAt);

            holder.txtReservTime.setText(localReservTime);
            holder.txtCreatedAt.setText(localCreatedAt);

            // 현재 시간과 비교
            Date currentTime = new Date();
            Date localReservDateTime = df.parse(localReservTime);
            if (localReservDateTime.before(currentTime)) {
                // 예약 시간이 현재 시간 이전인 경우
                holder.txtReservTime.setTextColor(ContextCompat.getColor(context, R.color.past_color));
            } else {
                // 예약 시간이 현재 시간 이후인 경우
                holder.txtReservTime.setTextColor(ContextCompat.getColor(context, R.color.future_color));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // 가격
        int priceSum = orderInfo.getPriceSum();
        if (priceSum == -1) {
            holder.txtPriceSum.setText("가격 정보 없음");
            holder.txtPriceSum.setTextColor(ContextCompat.getColor(context, R.color.fail_color));
        } else {
            holder.txtPriceSum.setText(String.valueOf(priceSum));
        }

        // 방문 여부
        int isVisited = orderInfo.getIsVisited();
        if (isVisited == 0) {
            holder.txtIsVisited.setText("미방문");
            holder.txtIsVisited.setTextColor(ContextCompat.getColor(context, R.color.fail_color));
            // 리뷰 작성 버튼 숨기기
            holder.btnReview.setVisibility(View.GONE);
        } else {
            holder.txtIsVisited.setText("방문");
            holder.txtIsVisited.setTextColor(ContextCompat.getColor(context, R.color.success_color));
            // 리뷰 작성 버튼 보이기
            holder.btnReview.setVisibility(View.VISIBLE);
        }

        // 메뉴
        Menu firstMenu = menuInfo.get(0);
        String menuText = firstMenu.getMenuName() + " " + firstMenu.getCount() + "개";
        int additionalMenus = menuInfo.size() - 1;
        if (additionalMenus > 0) {
            menuText += " 외 " + additionalMenus + "종";
        }
        holder.txtMenu.setText(menuText);


    }

    @Override
    public int getItemCount() {
        return myOrderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imgRestaurant;
        TextView txtName;
        Button btnReview;
        TextView txtReservTime;
        TextView txtMenu;
        TextView txtPriceSum;
        TextView txtCreatedAt;
        TextView txtIsVisited;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            txtName = itemView.findViewById(R.id.txtName);
            btnReview = itemView.findViewById(R.id.btnReview);
            txtReservTime = itemView.findViewById(R.id.txtReservTime);
            txtMenu = itemView.findViewById(R.id.txtMenu);
            txtPriceSum = itemView.findViewById(R.id.txtPriceSum);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtIsVisited = itemView.findViewById(R.id.txtIsVisited);
        }
    }

}
