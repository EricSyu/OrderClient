package com.example.ykk.orderclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Louis on 2016/4/7.
 */
public class SetMenu extends Thread {
    private String Server_IP;
    private int port;

    public SetMenu(String Server_IP, int port) {
        this.Server_IP = Server_IP;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(Server_IP, port);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int menu_num = 0;

            bw.write(1);
            bw.flush();

            menu_num = br.read();
            for (int i = 0; i < menu_num; i++) {
                String dish_name = br.readLine();
                int dish_price = br.read();
                Dish dish = new Dish(dish_name, dish_price, 0);
                MainActivity.MenuList.add(dish);
            }
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
