package com.reodinas2.eatopiaapp.model;

import java.util.List;

public class MyOrderList {
    String result;
    List<MyOrder> items;
    int count;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<MyOrder> getItems() {
        return items;
    }

    public void setItems(List<MyOrder> items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
