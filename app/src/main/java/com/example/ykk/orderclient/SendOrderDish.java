package com.example.ykk.orderclient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by Louis on 2016/4/7.
 */
public class SendOrderDish extends Thread {
    private String Server_IP;
    private int port, TableNum;
    private LinkedList<Dish> OrderDishList;

    public SendOrderDish(String Server_IP, int port, int TableNum, LinkedList<Dish> OrderDishList) {
        this.Server_IP = Server_IP;
        this.port = port;
        this.TableNum = TableNum;
        this.OrderDishList = OrderDishList;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(Server_IP, port);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(2);
            bw.flush();
            bw.write(TableNum);
            bw.flush();
            bw.write(OrderDishList.size());
            bw.flush();
            for (int i = 0; i < OrderDishList.size(); i++) {
                bw.write(OrderDishList.get(i).getName() + "x" + String.valueOf(OrderDishList.get(i).getAmount()));
                bw.flush();
            }
            /*******************++++++總價*********************/
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
