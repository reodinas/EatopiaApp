package com.reodinas2.eatopiaapp.model;

import java.util.ArrayList;
import java.util.List;

public class MyOrder {
    private Order orderInfo;
    private List<Menu> menuInfo;
    private Restaurant restaurantInfo;

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
}
