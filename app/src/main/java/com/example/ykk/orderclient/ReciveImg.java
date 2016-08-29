package com.example.ykk.orderclient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Louis on 2016/6/16.
 */
public class ReciveImg extends Thread {
    String dish_name;
    byte[] Img;
    Handler handler;

    public ReciveImg(String dish_name, Handler handler){
        this.dish_name = dish_name;
        this.handler = handler;
    }

    @Override
    public void run(){
        try {
            Socket socket = new Socket(MainActivity.Server_IP, MainActivity.port);
            Log.i("ReceiveImg", "Connect");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            int imgSize = 0;
            dos.writeInt(4);
            dos.flush();
            bw.write(dish_name + "\n");
            bw.flush();
            imgSize = dis.readInt();
            Img = new byte[imgSize];
            Log.i("ReceiveImg", Img.length + "");
            if (imgSize>0){
                dis.readFully(Img, 0, Img.length);
            }

            socket.close();
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getImg() {
        return Img;
    }
}
