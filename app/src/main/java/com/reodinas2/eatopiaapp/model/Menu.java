package com.reodinas2.eatopiaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Menu implements Parcelable {
    private int id;
    private int restaurantId;
    private String menuName;
    private int price;
    private String description;
    private String imgUrl;
    private String createdAt;
    private String updatedAt;

    // 메뉴수량
    private int count;

    public Menu() {
    }

    // Parcelable 인터페이스 구현
    protected Menu(Parcel in) {
        id = in.readInt();
        restaurantId = in.readInt();
        menuName = in.readString();
        price = in.readInt();
        description = in.readString();
        imgUrl = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        count = in.readInt();
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(restaurantId);
        dest.writeString(menuName);
        dest.writeInt(price);
        dest.writeString(description);
        dest.writeString(imgUrl);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeInt(count);
    }


    // getter/setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
