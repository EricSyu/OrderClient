package com.example.ykk.orderclient;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Louis on 2016/4/7.
 */
public class SendOrderDish extends Thread {
    private static final String TAG = SendOrderDish.class.getSimpleName();

    private String Server_IP;
    private int port, TableNum, price;
    private ArrayList<Dish> OrderDishList;
    private boolean InOrOutFlag;
    private int takeAwayNum;

    public SendOrderDish(String Server_IP, int port, int TableNum, ArrayList<Dish> OrderDishList, int price, boolean InOrOutFlag) {
        this.Server_IP = Server_IP;
        this.port = port;
        this.TableNum = TableNum;
        this.OrderDishList = OrderDishList;
        this.price = price;
        this.InOrOutFlag = InOrOutFlag;
    }

    @Override
    public void run() {
        try {
            Log.e(TAG, Server_IP + " " + port);
            Socket socket = new Socket(Server_IP, port);
            Log.e(TAG, "Connected");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeInt(2);
            dos.flush();
            dos.writeInt(TableNum);
            dos.flush();
            Log.e(TAG, "Price: " + price);
            dos.writeInt(price);
            dos.flush();
            String send_msg = "";
            for (int i = 0; i < OrderDishList.size(); i++) {
                send_msg += OrderDishList.get(i).getName() + "x" + OrderDishList.get(i).getAmount() + (i == OrderDishList.size() - 1 ? "\n" : " ");
            }
            Log.e(TAG, "Send msg:" + send_msg);
            bw.write(send_msg);
            bw.flush();

            if(!InOrOutFlag){
                takeAwayNum = dis.readInt();
            }
            Log.e(TAG, "Num:" + takeAwayNum);

            socket.close();
            Log.e(TAG, "Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTakeAwayNum(){
        return takeAwayNum;
    }
}
