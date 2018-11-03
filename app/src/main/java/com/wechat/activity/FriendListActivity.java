package com.wechat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wechat.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {
    private String me;
    private String friends;
    private List<String> friendsList;
    private ListView friendLv;
    private ArrayAdapter<String> adapter;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_list);
        init();

        final Intent intent = new Intent(this, ChatActivity.class);
        // 设置Listview监听
        friendLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friendName = friendsList.get(position);
                intent.putExtra("friendName", friendName);
                intent.putExtra("me", me);
                startActivity(intent);
            }
        });
    }

    private void init() {
        me = getIntent().getStringExtra("me");
        Log.i("fffff", me);

        // 更新Listview
        friendLv = findViewById(R.id.friendLv);
        friendsList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendsList);
        friendLv.setAdapter(adapter);

        //动态注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.wechat.activity.RECEIVER");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                friendsList.clear();
                friends = intent.getStringExtra("friends");
                String[] friendStr = friends.split(":");
                for (int i = 1; i < friendStr.length - 1; i++) {
                    if (!me.equals(friendStr[i]))
                        friendsList.add(friendStr[i]);
                }

                adapter.notifyDataSetChanged();
            }
        };
        registerReceiver(receiver, intentFilter);
    }

    public void quit(View view) {
        Intent intent = new Intent();
        intent.setAction("com.wechat.RECEIVER");
        intent.putExtra("bye","bye");
        sendBroadcast(intent);

        Intent intent1 = new Intent(this,LoginActivity.class);
        startActivity(intent1);
    }
}
