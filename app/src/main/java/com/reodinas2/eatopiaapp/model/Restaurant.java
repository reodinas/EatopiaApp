package com.reodinas2.eatopiaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Restaurant implements Parcelable {
    private int id;
    private String name;
    private String category;
    private String locCity;
    private String locDistrict;
    private String locDetail;
    private Double longitude;
    private Double latitude;
    private String imgUrl;
    private int cnt;
    private Double avg;
    private int distance;
    private String tel;
    private String summary;
    private String createdAt;
    private String updatedAt;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocCity() {
        return locCity;
    }

    public void setLocCity(String locCity) {
        this.locCity = locCity;
    }

    public String getLocDistrict() {
        return locDistrict;
    }

    public void setLocDistrict(String locDistrict) {
        this.locDistrict = locDistrict;
    }

    public String getLocDetail() {
        return locDetail;
    }

    public void setLocDetail(String locDetail) {
        this.locDetail = locDetail;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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


    // 생성자에서 Parcel 객체를 받아 멤버 변수 초기화
    protected Restaurant(Parcel in) {
        id = in.readInt();
        name = in.readString();
        category = in.readString();
        locCity = in.readString();
        locDistrict = in.readString();
        locDetail = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        imgUrl = in.readString();
        cnt = in.readInt();
        avg = in.readDouble();
        distance = in.readInt();
        tel = in.readString();
        summary = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(locCity);
        dest.writeString(locDistrict);
        dest.writeString(locDetail);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(imgUrl);
        dest.writeInt(cnt);
        dest.writeDouble(avg);
        dest.writeInt(distance);
        dest.writeString(tel);
        dest.writeString(summary);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);

    }
}
