package com.example.ykk.orderclient;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by Louis on 2016/4/7.
 */
public class SendOrderDish extends Thread {
    private static final String TAG = SendOrderDish.class.getSimpleName();

    private String Server_IP;
    private int port, TableNum, price;
    private LinkedList<Dish> OrderDishList;

    public SendOrderDish(String Server_IP, int port, int TableNum, LinkedList<Dish> OrderDishList, int price) {
        this.Server_IP = Server_IP;
        this.port = port;
        this.TableNum = TableNum;
        this.OrderDishList = OrderDishList;
        this.price = price;
    }

    @Override
    public void run() {
        try {
            Log.e(TAG, Server_IP + " " + port);
            Socket socket = new Socket(Server_IP, port);
            Log.e(TAG, "Connected");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(2);
            dos.flush();
            dos.writeInt(TableNum);
            dos.flush();
            Log.e(TAG, "Price: " + price);
            dos.writeInt(price);
            dos.flush();
            String send_msg = "";
            for (int i = 0; i < OrderDishList.size(); i++) {
                send_msg += OrderDishList.get(i).getName() + "x" + OrderDishList.get(i).getAmount() + (i == OrderDishList.size()-1 ? "\n" : " ");
            }
            Log.e(TAG, "Send msg:" + send_msg);

            bw.write(send_msg);
            bw.flush();
//            dos.write(send_msg.getBytes());
//            dos.flush();

//            bw.write(2);
//            bw.flush();
//            bw.write(TableNum);
//            bw.flush();
//            bw.write(OrderDishList.size());
//            bw.flush();
//            for (int i = 0; i < OrderDishList.size(); i++) {
//                bw.write(OrderDishList.get(i).getName() + "x" + String.valueOf(OrderDishList.get(i).getAmount()));
//                bw.flush();
//            }
//            bw.write(price);
//            bw.flush();

            socket.close();
            Log.e(TAG, "Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
