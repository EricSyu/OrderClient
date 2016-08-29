package com.example.ykk.orderclient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Louis on 2016/4/7.
 */
public class SetMenu extends Thread {
    private static final String TAG = SetMenu.class.getSimpleName();

    private String Server_IP;
    private int port;
    private ArrayList<ArrayList<Dish>> SortedMenuList;
    private List<Map<String, String>> groups;
    private ArrayList<String> HotDishes;
    private Handler handler;

    public SetMenu(String Server_IP, int port, Handler handler, ArrayList<ArrayList<Dish>> SortedMenuList,
                   List<Map<String, String>> groups, ArrayList<String> HotDishes) {
        this.Server_IP = Server_IP;
        this.port = port;
        this.handler = handler;
        this.SortedMenuList = SortedMenuList;
        this.groups = groups;
        this.HotDishes = HotDishes;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(Server_IP, port);
            Log.e(TAG, "Connected");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            /**************ReceiveMenu******************/
            int menu_num = 0;
            dos.writeInt(1);
            dos.flush();
            menu_num = dis.readInt();
            Log.e(TAG, "MenuNum: " + menu_num);
            ArrayList<Dish> menuList = new ArrayList<Dish>();
            HashSet<String> set = new HashSet();
            for (int i = 0; i <= menu_num; i++) {
                String recivemsg = br.readLine();
                if (i != menu_num) {
                    String dish_name = recivemsg.split(" ")[0];
                    Log.e(TAG, "DishName: " + dish_name);
                    String dish_type = recivemsg.split(" ")[1];
                    Log.e(TAG, "DishType: " + dish_type);
                    int dish_price = Integer.valueOf(recivemsg.split(" ")[2]);
                    Log.e(TAG, "DishPrice: " + dish_price);
                    Dish dish = new Dish(dish_name, dish_price, dish_type);
                    menuList.add(dish);

                    set.add(dish_type);
                } else {
                    Log.e(TAG, "HotDish: " + recivemsg);
                    for (int j = 0; j < recivemsg.split(" ").length; j++) {
                        HotDishes.add(recivemsg.split(" ")[j]);
                        Log.e(TAG, "HotDish: " + recivemsg.split(" ")[j]);
                    }
                }
            }

            socket.close();
            Log.e(TAG, "Closed");

            for (String type : set) {
                ArrayList<Dish> childMenuList = new ArrayList<>();
                for (Dish dish : menuList) {
                    if (type.equals(dish.getType())) {
                        childMenuList.add(dish);
                    }
                }
                SortedMenuList.add(childMenuList);
                Map<String, String> group1 = new HashMap<>();
                group1.put("group", type);
                groups.add(group1);
            }

            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
