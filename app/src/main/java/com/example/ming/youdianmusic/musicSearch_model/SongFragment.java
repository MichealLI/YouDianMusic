package com.example.ming.youdianmusic.musicSearch_model;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ming.youdianmusic.R;


/**
 * Created by Ming on 2017/6/6.
 */

public class SongFragment extends Fragment {
    public ListView songListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.songfragment, null);
        songListView = (ListView) view.findViewById(R.id.downloadList);
        return view;

    }
}
