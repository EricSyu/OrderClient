package com.example.ykk.orderclient;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Louis on 2016/4/3.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Map<String, String>> groups;
    private ArrayList<ArrayList<Dish>> SortedMenuList;
    private ArrayList<String> HotDishes;
    ReciveImg thread;
    ImageView picture;

    /*
    * 構造函數:
    * 參數1:context物件
    * 參數2:一級清單資料來源
    * 參數3:二級清單資料來源
    */

    public ExpandableAdapter(Context context, List<Map<String, String>> groups,
                             ArrayList<ArrayList<Dish>> SortedMenuList, ArrayList<String> HotDishes) {
        this.context = context;
        this.groups = groups;
        this.SortedMenuList = SortedMenuList;
        this.HotDishes = HotDishes;
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

        final Dish dish = (Dish)getChild(groupPosition, childPosition);

        for(String hotdishes : HotDishes){
//            Log.i("Update: ", hotdishes + " " + dish.getName());
            if(hotdishes.equals(dish.getName())){
                imageView.setImageResource(R.drawable.crown);
            }
        }
        dishName.setText(dish.getName());
        dishPrice.setText(dish.getPrice() + "");
        orderCount.setText(dish.getAmount() + "");
//        Log.e("ccc", groupPosition + " " + childPosition);
//        Log.e("SetMe", dish.getName());
//        Log.e("SetMe", dish.getPrice() + "");
//        Log.e("SetMe", dish.getAmount() + "");

        clickDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String dish_name = dish.getName();
                final int dish_price = dish.getPrice();

                thread = new ReciveImg(dish_name, mHandler);
                thread.start();

                LayoutInflater inflater = LayoutInflater.from(context);
                final View diav = inflater.inflate(R.layout.dia_information, null);

                final TextView diaTitle = (TextView) diav.findViewById(R.id.information_title);
                final TextView tvPrice = (TextView) diav.findViewById(R.id.show_price);
                picture = (ImageView) diav.findViewById(R.id.img_dish);

                diaTitle.setText(dish_name);
                tvPrice.setText(String.valueOf(dish_price));

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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    byte[] tmp = thread.getImg();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
                    Picasso.with(context).load(getImageUri(context, bitmap)).resize(500, 600).into(picture);
                    break;
            }
        }
    };

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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