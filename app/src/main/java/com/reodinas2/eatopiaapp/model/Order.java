package com.reodinas2.eatopiaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Order implements Parcelable {
    private int id;
    private int userId;
    private int restaurantId;
    private int people;
    private String reservTime;
    private int type;
    private String createdAt;
    private int isVisited;
    private int priceSum;

    public Order() {
    }

    protected Order(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        restaurantId = in.readInt();
        people = in.readInt();
        reservTime = in.readString();
        type = in.readInt();
        createdAt = in.readString();
        isVisited = in.readInt();
        priceSum = in.readInt();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public String getReservTime() {
        return reservTime;
    }

    public void setReservTime(String reservTime) {
        this.reservTime = reservTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getIsVisited() {
        return isVisited;
    }

    public void setIsVisited(int isVisited) {
        this.isVisited = isVisited;
    }

    public int getPriceSum() {
        return priceSum;
    }

    public void setPriceSum(int priceSum) {
        this.priceSum = priceSum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(restaurantId);
        dest.writeInt(people);
        dest.writeString(reservTime);
        dest.writeInt(type);
        dest.writeString(createdAt);
        dest.writeInt(isVisited);
        dest.writeInt(priceSum);
    }
}
