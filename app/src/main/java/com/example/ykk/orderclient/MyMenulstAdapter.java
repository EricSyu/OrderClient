package com.example.ykk.orderclient;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Louis on 2016/4/7.
 */
public class MyMenulstAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    private ArrayList<Dish> MenuList;

    public MyMenulstAdapter(Context c, ArrayList<Dish> MenuList) {
        myInflater = LayoutInflater.from(c);
        this.MenuList = MenuList;
    }

    @Override
    public int getCount() {
        return MenuList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = myInflater.inflate(R.layout.menu_list, null);
        TextView dishname = (TextView) convertView.findViewById(R.id.tv_dishname);
        TextView dishprice = (TextView) convertView.findViewById(R.id.tv_dishprice);
        Log.e("SetMe", "" + MenuList.size());
        dishname.setText(MenuList.get(position).getName());
        Log.e("SetMe", MenuList.get(position).getName());
        dishprice.setText(MenuList.get(position).getPrice() + "");

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
