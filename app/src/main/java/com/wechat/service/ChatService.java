package com.wechat.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.wechat.activity.FriendListActivity;
import com.wechat.dataBase.MyDBHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

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
    private BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        init();

        // 接受服务器数据
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        while ((receiverMessage = bReader.readLine()) != null) {
                            if (receiverMessage.startsWith("&")) {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent();
                                intent.setAction("com.wechat.activity.RECEIVER");
                                intent.putExtra("friends", receiverMessage);
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
        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    socket = new Socket("47.106.208.254", 23);
                    bReader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    pWriter = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        helper = new MyDBHelper(this, "Message", null, 1);
        db = helper.getWritableDatabase();

        isFirst = true;

        //动态注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.wechat.RECEIVER");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String bye = intent.getStringExtra("bye");
                Log.i("bye","bye");
                pWriter.println(bye);
            }
        };
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isFirst) {
            isFirst = false;
            userName = intent.getStringExtra("userName");
            new Thread(){
                @Override
                public void run() {
                    pWriter.println(userName);
                }
            }.start();

        } else {
            sendMessage = intent.getStringExtra("message");
            sendWords = sendMessage.split(":");
            updateDatabase(sendWords);
            new Thread(){
                @Override
                public void run() {
                    pWriter.println(sendMessage);
                }
            }.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 判断某个Activity 界面是否在前台
     * @param context
     * @param className 某个界面名称
     * @return
     */
    public boolean  isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
