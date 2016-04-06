package com.example.ykk.orderclient;

/**
 * Created by Louis on 2016/4/5.
 */
public class Dish {
    private String name;
    private int price;
    private int num;

    public Dish(String name) {
        this.name = name;
        this.price = 0;
        this.num = 0;
    }

    public String getName() {
        return name;
    }

    public int getPrice(){
        return price;
    }

    public int getNum() {
        return num;
    }

    public void setPrice(int price){
        this.price = price;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
