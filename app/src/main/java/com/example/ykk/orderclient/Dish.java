package com.example.ykk.orderclient;

/**
 * Created by Louis on 2016/4/5.
 */
public class Dish {
    private String name;
    private int price;
    private int amount;
    private String type;

    public Dish(String name, int price, String type) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.amount = 0;
    }

    public Dish(String name, int price, String type, int amount) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public int getPrice(){
        return price;
    }

    public String getType(){
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setType(String type){
        this.type = type;
    }
}
