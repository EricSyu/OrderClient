package com.example.ykk.orderclient;

/**
 * Created by Louis on 2016/4/5.
 */
public class Dish {
    private String name;
    private int price;
    private int amount;

    public Dish(String name, int price) {
        this.name = name;
        this.price = price;
        this.amount = 0;
    }

    public Dish(String name, int price, int amount) {
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public int getPrice(){
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
