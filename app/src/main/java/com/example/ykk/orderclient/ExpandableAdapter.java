package com.example.ykk.orderclient;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Louis on 2016/4/3.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    List<Map<String, String>> groups;
    ArrayList<ArrayList<Dish>> SortedMenuList;

    /*
    * 構造函數:
    * 參數1:context物件
    * 參數2:一級清單資料來源
    * 參數3:二級清單資料來源
    */

    public ExpandableAdapter(Context context,  ArrayList<ArrayList<Dish>> SortedMenuList, List<Map<String, String>> groups) {
        this.context = context;
        this.SortedMenuList = SortedMenuList;
        this.groups = groups;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return SortedMenuList.get(groupPosition).get(childPosition);
    }

    public int getChildrenCount(int groupPosition) {
        return SortedMenuList.get(groupPosition).size();
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //獲取二級清單的View物件

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        @SuppressWarnings("unchecked")
        //String text = ((Map<String, String>) getChild(groupPosition, childPosition)).get("child");
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //獲取二級清單對應的佈局檔, 並將其各元素設置相應的屬性
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.child, null);

        LinearLayout clickDish = (LinearLayout) linearLayout.findViewById(R.id.lin_click);
        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.imageView);
        TextView dishName = (TextView) linearLayout.findViewById(R.id.tv_dishName);
        TextView dishPrice = (TextView) linearLayout.findViewById(R.id.tv_dishPrice);
        TextView orderCount = (TextView) linearLayout.findViewById(R.id.text_amount);
        ImageButton btnAdd = (ImageButton) linearLayout.findViewById(R.id.btn_add);
        ImageButton btnReduce = (ImageButton) linearLayout.findViewById(R.id.btn_reduce);

        //if(人氣菜點){imageView.setImageResource(R.drawable.crown);}

        final Dish dish = (Dish)getChild(groupPosition, childPosition);
        dishName.setText(dish.getName());
        dishPrice.setText(dish.getPrice() + "");
        orderCount.setText(dish.getAmount() + "");
        Log.e("ccc", groupPosition + " " + childPosition);
        Log.e("SetMe", dish.getName());
        Log.e("SetMe", dish.getPrice() + "");
        Log.e("SetMe", dish.getAmount() + "");

        clickDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(context);
                final View diav = inflater.inflate(R.layout.dia_information, null);

                final String dish_name = dish.getName();
                final int dish_price = dish.getPrice();
                final TextView diaTitle = (TextView) diav.findViewById(R.id.information_title);
                final TextView tvPrice = (TextView) diav.findViewById(R.id.show_price);
                final ImageView picture = (ImageView) diav.findViewById(R.id.img_dish);

                diaTitle.setText(dish_name);
                tvPrice.setText(String.valueOf(dish_price));

                /*******************************/
                //Image.setImageResource();
                /*******************************/

                final AlertDialog.Builder dishDialog = new AlertDialog.Builder(context);
                dishDialog.setView(diav);
                dishDialog.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = dish.getAmount() + 1;
                dish.setAmount(amount);
                notifyDataSetChanged();
            }
        });
        btnReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount2 = dish.getAmount();
                if (amount2 != 0) {
                    amount2 = amount2 - 1;
                    dish.setAmount(amount2);
                    notifyDataSetChanged();
                }
            }
        });
        return linearLayout;
    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //獲取一級清單View物件
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String text = groups.get(groupPosition).get("group");
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //獲取一級清單佈局檔,設置相應元素屬性
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.group, null);

        TextView dishCategory = (TextView) linearLayout.findViewById(R.id.tv_category);
        dishCategory.setText(text); //"category" );//********  DB
        return linearLayout;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}