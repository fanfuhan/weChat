package com.wechat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    public ChatContentAdapter(@NonNull Context context, int resource, List<ChatContent> chatList,String me) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.chatList = chatList;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        //将资源ID变成View
        View view=LayoutInflater
                .from(getContext())
                .inflate(resource,parent,false);


//        TextView nameTv=view.findViewById(R.id.name);
//        TextView priceTv=view.findViewById(R.id.price);
//        convertView.findViewById(R.id.layout_left).setVisibility(View.VISIBLE);
//        convertView.findViewById(R.id.layout_right).setVisibility(View.VISIBLE);
//        if(cc.isMe()){
//            convertView.findViewById(R.id.layout_left).setVisibility(View.GONE);
//           TextView tv= convertView.findViewById(R.id.content_right);
//           tv.setText(cc.getContent());
//        }
//        else{
//            convertView.findViewById(R.id.layout_right).setVisibility(View.GONE);
//            TextView tv= convertView.findViewById(R.id.content_left);
//            tv.setText(cc.getContent());
//        }


        return convertView;
    }
}