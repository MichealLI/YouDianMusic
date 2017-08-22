package com.example.ming.youdianmusic.musicSearch_model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ming.youdianmusic.R;
import com.example.ming.youdianmusic.common.Music;
import com.example.ming.youdianmusic.musicDownload_model.DownLoadActivity;
import com.example.ming.youdianmusic.musicPlay_model.MusicPlayerActivity;

import java.util.List;

/**
 * Created by Ming on 2017/6/5.
 */

public class RmusicListAdapter extends BaseAdapter {
    private List<Music> mlist;
    private Context context;

    public RmusicListAdapter(Context context, List mlist) {
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
       final Music music = mlist.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.netmusiclist_item, null);
            viewHolder = new ViewHolder();
            viewHolder.music_name = (TextView) view.findViewById(R.id.music_name);
            viewHolder.singer = (TextView) view.findViewById(R.id.singer);
            viewHolder.try_listen = (ImageView) view.findViewById(R.id.try_listen);
            viewHolder.try_listen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到音乐播放界面
                    Intent intent = new Intent(context, MusicPlayerActivity.class);
                    intent.putExtra("isLocal", false);
                    intent.putExtra("music", music);
                    context.startActivity(intent);
                }
            });
            viewHolder.download = (ImageView) view.findViewById(R.id.download);
            viewHolder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到音乐下载界面
                    Intent intent = new Intent(context, DownLoadActivity.class);
                    intent.putExtra("music", music);
                    context.startActivity(intent);
                }
            });
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();

        }
        viewHolder.music_name.setText(music.getMusicName());
        viewHolder.singer.setText(music.getArtistName());
        return view;
    }

    class ViewHolder {
        TextView music_name;
        TextView singer;
        ImageView try_listen;
        ImageView download;
    }

}
