package com.example.ykk.orderclient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Louis on 2016/4/7.
 */
public class SendBell extends Thread {
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
            Socket socket = new Socket(Server_IP, port);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bw.write(3);
            bw.flush();
            bw.write(TableNum);
            bw.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
