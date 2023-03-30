package com.reodinas2.eatopiaapp.model;

public class UserInfoRes {

    private String result;
    private User userInfo;
    private String imgUrl;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public User getUser() {
        return userInfo;
    }

    public void setUser(User user) {
        this.userInfo = user;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
