package com.reodinas2.eatopiaapp.model;

import java.util.List;

public class OrderRes {
    private String result;
    private Order orderInfo;
    private List<Menu> menuInfo;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

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
}
