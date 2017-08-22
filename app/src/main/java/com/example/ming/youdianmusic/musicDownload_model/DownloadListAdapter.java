package com.example.ming.youdianmusic.musicDownload_model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ming.youdianmusic.R;

import java.util.List;

/**
 * Created by Ming on 2017/6/3.
 */

public class DownloadListAdapter extends BaseAdapter {

    private List<String> mlist;
    private Context context;

    public DownloadListAdapter(Context context, List mlist) {
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
        String name = mlist.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.downloadmusic_item, null);
            viewHolder = new ViewHolder();
            viewHolder.music_name = (TextView) view.findViewById(R.id.music_name);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();

        }
        viewHolder.music_name.setText(name);

        return view;
    }

    class ViewHolder {
        TextView music_name;
    }

}