package com.lee.android.MDMP.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lee.android.MDMP.R;
import com.lee.android.MDMP.Model.Song;

import java.util.List;

public class MyListAdapter extends BaseAdapter {
    private Context context;
    private List<Song> list;
    private int index;

    public MyListAdapter(Context context, List<Song> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_song_listview, null);   //引入布局
            holder.songname = (TextView) convertView.findViewById(R.id.item_songname);
            holder.singer = (TextView) convertView.findViewById(R.id.item_singer);
            holder.time = (TextView) convertView.findViewById(R.id.item_time);
            convertView.setTag(holder);   //把holder附加到view上
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //给各item中的textview赋值
        holder.songname.setText(list.get(position).getName());
        holder.singer.setText(list.get(position).getSinger());
        holder.time.setText(list.get(position).getTime());
        return convertView;
    }
}

class ViewHolder {     //减少 findViewById() 的使用，避免过多地 inflate view
    public TextView songname;
    public TextView singer;
    public TextView time;
}