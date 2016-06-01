package com.example.ykk.orderclient;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Louis on 2016/4/7.
 */
public class SendBell extends Thread {
    private static final String TAG = SendBell.class.getSimpleName();

    private String Server_IP;
    private int port, TableNum;

    public SendBell(String Server_IP, int port, int TableNum) {
        this.Server_IP = Server_IP;
        this.port = port;
        this.TableNum = TableNum;
    }

    @Override
    public void run() {
        try {
            Log.e(TAG, Server_IP + " " + port);
            Socket socket = new Socket(Server_IP, port);
            Log.e(TAG, "Connected");
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(3);
            dos.flush();
            dos.writeInt(TableNum);
            dos.flush();

            socket.close();
            Log.e(TAG, "Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
