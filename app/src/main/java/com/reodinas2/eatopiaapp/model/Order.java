package com.reodinas2.eatopiaapp.model;

import java.util.List;

public class Order {
    private int people;
    private String reservTime;
    private int type;
    private List<Menu> menuInfo;


    public Order() {
    }

    public Order(int people, String reservTime, int type, List<Menu> menuInfo) {
        this.people = people;
        this.reservTime = reservTime;
        this.type = type;
        this.menuInfo = menuInfo;
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

    public List<Menu> getMenuInfo() {
        return menuInfo;
    }

    public void setMenuInfo(List<Menu> menuInfo) {
        this.menuInfo = menuInfo;
    }
}
