package com.wechat.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.wechat.dataBase.MyDBHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatService extends Service {
    private Socket socket;
    private BufferedReader bReader;
    private PrintWriter pWriter;
    private MyDBHelper helper;
    private SQLiteDatabase db;
    private String userName;
    private boolean isFirst;
    private String sendMessage;
    private String receiverMessage;
    private String[] receiveWords;
    private String[] sendWords;

    @Override
    public void onCreate() {
        super.onCreate();
        init();

        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        while ((receiverMessage = bReader.readLine()) != null) {
                            if (receiverMessage.startsWith("&")) {
                                Intent intent = new Intent();
                                intent.putExtra("friends", receiverMessage);
                                intent.putExtra("me", userName);
                                intent.setAction("com.wechat.RECEIVER");
                                sendBroadcast(intent);
                            } else {
                                receiveWords = receiverMessage.split(":");
                                updateDatabase(receiveWords);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private synchronized void updateDatabase(String[] wordparts) {
            ContentValues cv = new ContentValues();
            cv.put("sender", wordparts[0]);
            cv.put("receiver", wordparts[1]);
            cv.put("content", wordparts[2]);

            db.insert("message", null, cv);
    }

    private void init() {
        try {
            socket = new Socket("47.106.208.254", 23);
            bReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
            pWriter = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        helper = new MyDBHelper(this, "Message", null, 1);
        db = helper.getWritableDatabase();

        isFirst = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isFirst) {
            isFirst = false;
            userName = intent.getStringExtra("userName");
            pWriter.println(userName);
        } else {
            sendMessage = intent.getStringExtra("message");
            sendWords = receiverMessage.split(":");
            updateDatabase(sendWords);
            pWriter.println(sendMessage);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
