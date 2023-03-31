package com.reodinas2.eatopiaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MyOrder implements Parcelable {
    private Order orderInfo;
    private List<Menu> menuInfo;
    private Restaurant restaurantInfo;

    public MyOrder() {
    }

    protected MyOrder(Parcel in) {
        orderInfo = in.readParcelable(Order.class.getClassLoader());
        menuInfo = new ArrayList<>();
        in.readList(menuInfo, Menu.class.getClassLoader());
        restaurantInfo = in.readParcelable(Restaurant.class.getClassLoader());
    }

    public static final Creator<MyOrder> CREATOR = new Creator<MyOrder>() {
        @Override
        public MyOrder createFromParcel(Parcel in) {
            return new MyOrder(in);
        }

        @Override
        public MyOrder[] newArray(int size) {
            return new MyOrder[size];
        }
    };

    public Order getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(Order orderInfo) {
        this.orderInfo = orderInfo;
    }

    public List<Menu> getMenuInfo() {
        return menuInfo;
    }

    public void setMenuInfo(List<Menu> menuInfo) {
        this.menuInfo = menuInfo;
    }

    public Restaurant getRestaurantInfo() {
        return restaurantInfo;
    }

    public void setRestaurantInfo(Restaurant restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeParcelable(orderInfo, i);
        dest.writeList(menuInfo);
        dest.writeParcelable(restaurantInfo, i);
    }
}
