package com.example.ming.youdianmusic.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ming.youdianmusic.R;

import java.util.List;

/**
 * Created by Ming on 2017/6/5.
 */

public class MenuAdapter extends BaseAdapter{
    private Context context;
    private List<String> mlist;

    public MenuAdapter(Context context, List<String> mlist) {
        super();
        this.context = context;
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mlist.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int positon, View contentView, ViewGroup viewGroup) {
        View view;
        ViewHolder viewHolder;
        String menuItem = mlist.get(positon);
        if(contentView == null){
            view = LayoutInflater.from(context).inflate(R.layout.menu_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.menu_item);
            view.setTag(viewHolder);
        }else{
            view = contentView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(menuItem);
        return view;

    }
    class ViewHolder{
        TextView name;
    }
}
