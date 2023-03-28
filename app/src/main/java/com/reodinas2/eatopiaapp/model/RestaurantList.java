package com.reodinas2.eatopiaapp.model;

import java.util.List;

public class RestaurantList {

    private String result;
    private List<Restaurant> items;
    private int count;
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Restaurant> getItems() {
        return items;
    }

    public void setItems(List<Restaurant> items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
