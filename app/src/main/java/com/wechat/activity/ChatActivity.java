package com.wechat.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.wechat.R;
import com.wechat.adapter.ChatContentAdapter;
import com.wechat.bean.ChatContent;

import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private EditText editText;
    private Button sendButton;
    private ListView chatLv;
    private List<ChatContent> chatList;
    private ChatContentAdapter adapter;
    private String me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
    }

    private void init() {
        me = getIntent().getStringExtra("me");
    }

    public void leave(View view) {
    }
}
