package com.wechat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wechat.R;
import com.wechat.bean.ChatContent;

import java.util.List;

public class ChatContentAdapter extends ArrayAdapter<ChatContent> {
    private String me;
    private Context context;
    private int resource;
    private List<ChatContent> chatList;

    public ChatContentAdapter(@NonNull Context context, int resource, List<ChatContent> chatList) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.chatList = chatList;
    }

    public ChatContentAdapter(@NonNull Context context, int resource, List<ChatContent> chatList, String me) {
        this(context, resource, chatList);
        this.me = me;
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Nullable
    @Override
    public ChatContent getItem(int position) {
        return chatList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatContent chatContent = chatList.get(position);
        convertView = LayoutInflater.from(context).inflate(resource, parent, false);

        convertView.findViewById(R.id.layout_left).setVisibility(View.VISIBLE);
        convertView.findViewById(R.id.layout_right).setVisibility(View.VISIBLE);

        if (chatContent.getSender().equals(me)) {
            convertView.findViewById(R.id.layout_left).setVisibility(View.GONE);
            TextView tv = convertView.findViewById(R.id.content_right);
            tv.setText(chatContent.getContent());
            tv.setTextSize(22);
            tv.setBackgroundColor(Color.GREEN);
            //tv.setPadding(1000,0,0,0);
        } else {
            convertView.findViewById(R.id.layout_right).setVisibility(View.GONE);
            TextView tv = convertView.findViewById(R.id.content_left);
            tv.setText(chatContent.getContent());
            tv.setBackgroundColor(Color.GRAY);
            tv.setTextSize(22);
        }

        return convertView;
    }
}
