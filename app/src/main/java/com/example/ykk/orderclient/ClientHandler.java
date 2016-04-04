package com.example.ykk.orderclient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Louis on 2016/4/4.
 */
public class ClientHandler extends Thread {
    private static final String TAG = ClientHandler.class.getSimpleName();

    static Socket Client_socket;
    static int port = 6000;
    static String Server_IP = "192.168.1.106";

    public void run() {
        try {
            Log.i(TAG, "Thread");
            Client_socket = new Socket(Server_IP, port);
            while (Client_socket.isConnected()) {
                Log.i(TAG, "connected");
                BufferedReader br = new BufferedReader(new InputStreamReader(Client_socket.getInputStream()));
                MainActivity.hamburger[0] = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
