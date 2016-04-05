package com.example.ykk.orderclient;

/**
 * Created by Louis on 2016/4/5.
 */
public class OrderDish {
    private String name;
    private int price;
    private int num = 0;

    public OrderDish(String name, int num) {
        this.name = name;
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
