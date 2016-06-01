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

/**
 * Created by Louis on 2016/4/7.
 */
public class SetMenu extends Thread {
    private static final String TAG = SetMenu.class.getSimpleName();

    private String Server_IP;
    private int port;
    private MyMenuListAdapter menuListAdapter;
    private ArrayList<Dish> MenuList;
    private Handler handler;

    public SetMenu(String Server_IP, int port, Handler handler, ArrayList<Dish> MenuList) {
        this.Server_IP = Server_IP;
        this.port = port;
        this.handler = handler;
        this.MenuList = MenuList;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(Server_IP, port);
            Log.e(TAG, "Connected");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            int menu_num = 0;

            dos.writeInt(1);
            dos.flush();

            menu_num = dis.readInt();
            Log.e(TAG, "MenuNum: " + menu_num);
            ArrayList<Dish> menuList = new ArrayList<Dish>();
            for (int i = 0; i < menu_num; i++) {
                String recivemsg = br.readLine();
                String dish_name = recivemsg.split(" ")[0];
                Log.e(TAG, "DishName: " + dish_name);
                int dish_price = Integer.valueOf(recivemsg.split(" ")[1]);
                Log.e(TAG, "DishPrice: " + dish_price);
                Dish dish = new Dish(dish_name, dish_price);
                //MenuList.add(dish);
                menuList.add(dish);
            }
            socket.close();
            Log.e(TAG, "Closed");
//            for(int i=0; i<MenuList.size(); i++){
//                Log.e(TAG, MenuList.get(i).getName());
//                Log.e(TAG, "" + MenuList.get(i).getPrice());
//            }

            MenuList.clear();
            MenuList.addAll(menuList);

            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
