package com.wechat.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wechat.R;
import com.wechat.service.ChatService;

public class LoginActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        editText = findViewById(R.id.userName);
        button = findViewById(R.id.login);
    }

    public void login(View view) {
        // 获得用户名传到service
        userName = editText.getText().toString();
        Intent intentService = new Intent();
        intentService.putExtra("userName",userName);
        intentService.setClass(this,ChatService.class);
        startService(intentService);
        // 跳转到好友列表
        Intent intentActivity = new Intent(this,FriendListActivity.class);
        intentActivity.putExtra("me",userName);
        startActivity(intentActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
