package com.example.ming.youdianmusic.musicPlay_model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ming.youdianmusic.R;
import com.example.ming.youdianmusic.common.MusicInfo;

import java.util.List;

/**
 * Created by Ming on 2017/6/3.
 */

public class MusicListAdapter extends BaseAdapter {

    private List<MusicInfo> mlist;
    private Context context;

    public MusicListAdapter(Context context, List mlist) {
        this.context = context;
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewgroup) {
        MusicInfo musicInfo = mlist.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.musiclist_item, null);
            viewHolder = new ViewHolder();
            viewHolder.music_name = (TextView) view.findViewById(R.id.music_name);
            viewHolder.singer = (TextView) view.findViewById(R.id.singer);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();

        }
        viewHolder.music_name.setText(musicInfo.getName());
        viewHolder.singer.setText(musicInfo.getSinger());

        return view;
    }

    class ViewHolder {
        TextView music_name;
        TextView singer;
    }

}