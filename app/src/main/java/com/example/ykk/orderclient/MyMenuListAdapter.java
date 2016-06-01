package com.example.ykk.orderclient;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Louis on 2016/4/7.
 */
public class MyMenuListAdapter extends BaseAdapter{
    private LayoutInflater myInflater;
    private ArrayList<Dish> MenuList;

    public MyMenuListAdapter( Context c, ArrayList<Dish> MenuList) {
        myInflater = LayoutInflater.from(c);
        this.MenuList = MenuList;
    }

    @Override
    public int getCount() {
        return MenuList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        if(is category){
//            convertView = myInflater.inflate(R.layout.list_category, null);
//            TextView dishCategory = (TextView) convertView.findViewById(R.id.tv_category);
//            dishCategory.setText(  "category" );//DB
//
//        }else{
//            if(is hot){
//                convertView = myInflater.inflate(R.layout.list_dish_hot, null);
//                TextView dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
//                TextView dishPrice = (TextView) convertView.findViewById(R.id.tv_dishPrice);
//                TextView orderCount = (TextView) convertView.findViewById(R.id.text_amount);
//                ImageButton btnAdd = (ImageButton) convertView.findViewById(R.id.btn_add);
//                ImageButton btnReduce = (ImageButton) convertView.findViewById(R.id.btn_reduce);
//                Log.e("SetMe", "" + MenuList.size());
//                dishName.setText(MenuList.get(position).getName());
//                Log.e("SetMe", MenuList.get(position).getName());
//                dishPrice.setText(MenuList.get(position).getPrice() + "");
//                orderCount.setText(MenuList.get(position).getAmount() + "");
//                this.position = position;
//                btnAdd.setOnClickListener(this);
//                btnReduce.setOnClickListener(this);
//            }else{//is common
                convertView = myInflater.inflate(R.layout.list_dish_common, null);
                TextView dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
                TextView dishPrice = (TextView) convertView.findViewById(R.id.tv_dishPrice);
                TextView orderCount = (TextView) convertView.findViewById(R.id.text_amount);
                ImageButton btnAdd = (ImageButton) convertView.findViewById(R.id.btn_add);
                ImageButton btnReduce = (ImageButton) convertView.findViewById(R.id.btn_reduce);
                Log.e("SetMe", "" + MenuList.size());
                dishName.setText(MenuList.get(position).getName());
                Log.e("SetMe", MenuList.get(position).getName());
                dishPrice.setText(MenuList.get(position).getPrice() + "");
                orderCount.setText(MenuList.get(position).getAmount() + "");
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int amount = MenuList.get(position).getAmount()+1;
                        MenuList.get(position).setAmount(amount);
                        notifyDataSetChanged();
                    }
                });
                btnReduce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int amount2 = MenuList.get(position).getAmount();
                        if(amount2 != 0){
                            amount2 = amount2 - 1;
                            MenuList.get(position).setAmount(amount2);
                            notifyDataSetChanged();
                        }
                    }
                });

//        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return MenuList.get(position);
    }


}
