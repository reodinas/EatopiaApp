//package com.reodinas2.eatopiaapp.model;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import androidx.annotation.NonNull;
//
//import java.io.Serializable;
//
//public class MenuInfo implements Parcelable {
//    int menuId;
//    int count;
//    String menuName;
//    int price;
//    String description;
//    String imgUrl;
//
//    public MenuInfo() {
//    }
//
//    // Parcelable 인터페이스 구현
//    protected MenuInfo(Parcel in) {
//        menuId = in.readInt();
//        count = in.readInt();
//        menuName = in.readString();
//        price = in.readInt();
//        description = in.readString();
//        imgUrl = in.readString();
//    }
//
//    public static final Creator<MenuInfo> CREATOR = new Creator<MenuInfo>() {
//        @Override
//        public MenuInfo createFromParcel(Parcel in) {
//            return new MenuInfo(in);
//        }
//
//        @Override
//        public MenuInfo[] newArray(int size) {
//            return new MenuInfo[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(@NonNull Parcel parcel, int flags) {
//        parcel.writeInt(menuId);
//        parcel.writeInt(count);
//        parcel.writeString(menuName);
//        parcel.writeInt(price);
//        parcel.writeString(description);
//        parcel.writeString(imgUrl);
//    }
//
//    public int getMenuId() {
//        return menuId;
//    }
//
//    public void setMenuId(int menuId) {
//        this.menuId = menuId;
//    }
//
//    public int getCount() {
//        return count;
//    }
//
//    public void setCount(int count) {
//        this.count = count;
//    }
//
//    public String getMenuName() {
//        return menuName;
//    }
//
//    public void setMenuName(String menuName) {
//        this.menuName = menuName;
//    }
//
//    public int getPrice() {
//        return price;
//    }
//
//    public void setPrice(int price) {
//        this.price = price;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getImgUrl() {
//        return imgUrl;
//    }
//
//    public void setImgUrl(String imgUrl) {
//        this.imgUrl = imgUrl;
//    }
//
//
//}
