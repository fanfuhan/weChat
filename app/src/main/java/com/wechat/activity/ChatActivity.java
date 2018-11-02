package com.wechat.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wechat.R;
import com.wechat.adapter.ChatContentAdapter;
import com.wechat.bean.ChatContent;
import com.wechat.dataBase.MyDBHelper;
import com.wechat.service.ChatService;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private TextView friendNameView;
    private EditText sendMsgEdit;
    private ListView chatLv;
    private List<ChatContent> chatList;
    private ChatContentAdapter adapter;
    private String me;
    private String friendName;
    private String sendMsg;
    private StringBuilder tempMsg;
    private SQLiteDatabase db;
    private MyDBHelper helper;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateChatList();
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    adapter.notifyDataSetChanged();
                }
            }
        };

        initListView();
    }

    private void updateChatList() {
        String sql = "select * from message where sender=? or receiver=?";
        Cursor c = db.rawQuery(sql, new String[]{friendName, friendName});
        while (c.moveToNext()) {
            String sender = c.getString(c.getColumnIndex("sender"));
            String receiver = c.getString(c.getColumnIndex("receiver"));
            String content = c.getString(c.getColumnIndex("content"));

            ChatContent chatContent = new ChatContent(sender,receiver,content);
            chatList.add(chatContent);
        }
    }

    private void initListView() {
        adapter = new ChatContentAdapter(this, R.layout.chat_layout, chatList, me);
        chatLv.setAdapter(adapter);
    }

    private void init() {
        me = getIntent().getStringExtra("me");
        friendName = getIntent().getStringExtra("friendName");
        friendNameView = findViewById(R.id.friendName);
        friendNameView.setText(friendName);
        sendMsgEdit = findViewById(R.id.sendMsg);
        chatLv = findViewById(R.id.friendLv);
        chatList = new ArrayList<>();

        // 数据库
        helper = new MyDBHelper(this, "Message", null, 1);
        db = helper.getReadableDatabase();
    }

    public void leave(View view) {
        finish();
    }

    public void send(View view) {
        sendMsg = sendMsgEdit.getText().toString();
        if (sendMsg != null) {
            sendMsg = tempMsg.append(me).append(":").append(friendName).append(":").append(sendMsg).toString();
            Intent intent = new Intent(this, ChatService.class);
            intent.putExtra("message", sendMsg);
            startService(intent);
            sendMsgEdit.setText("");
        }
    }
}
