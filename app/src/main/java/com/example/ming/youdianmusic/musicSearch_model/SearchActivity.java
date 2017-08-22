package com.example.ming.youdianmusic.musicSearch_model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ming.youdianmusic.R;
import com.example.ming.youdianmusic.common.Music;
import com.example.ming.youdianmusic.util.OkhttpDownload;
import com.example.ming.youdianmusic.util.SetMusicListListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends FragmentActivity implements View.OnClickListener {
    private EditText search_content;
    private ImageView searchThing;
    private ListView mListView;
    private SongFragment songFragment;
    private SingerFragment singerFragment;
    private ViewPager myViewPager;
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private TextView singerTab;
    private TextView songTab;

    //更新UI界面
    public Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //更新歌曲搜索的fragment
                    final List<Music> musicList = (List<Music>) msg.obj;
                    RmusicListAdapter musicListAdapter = new RmusicListAdapter(SearchActivity.this, musicList);
                    songFragment.songListView.setAdapter(musicListAdapter);
                    break;
                case 1:
                    //更新歌手搜索的fragment
                    final List<Music> singerList = (List<Music>) msg.obj;
                    RmusicListAdapter musicListAdapter1 = new RmusicListAdapter(SearchActivity.this, singerList);
                    singerFragment.singerListView.setAdapter(musicListAdapter1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //绑定控件
        search_content = (EditText) findViewById(R.id.search_content);
        searchThing = (ImageView) findViewById(R.id.searchThing);
        myViewPager = (ViewPager) findViewById(R.id.myViewPager);
        singerTab = (TextView) findViewById(R.id.singertab);
        songTab = (TextView) findViewById(R.id.songtab);
        if (myViewPager == null)
            Log.e("ming", "空空空");
        searchThing.setOnClickListener(this);

        songFragment = new SongFragment();
        singerFragment = new SingerFragment();
        fragmentList.add(songFragment);
        fragmentList.add(singerFragment);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        myViewPager.setAdapter(fragmentPagerAdapter);
        myViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
//                    singerTab.setTextColor(Color.RED);
//                    songTab.setTextColor(Color.BLUE);
                    songTab.setTextColor(getResources().getColor(R.color.myBlue));
//                    songTab.setText("歌单1");
                    singerTab.setTextColor(getResources().getColor(R.color.normal));
//                    singerTab.setText("歌手1");
                } else if (position == 1) {
//                    singerTab.setTextColor(Color.BLUE);
//                    songTab.setTextColor(Color.RED);

                    singerTab.setTextColor(getResources().getColor(R.color.myBlue));

//                    songTab.setText("歌单1");
                    songTab.setTextColor(getResources().getColor(R.color.normal));
//                    songTab.setText("歌单2");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchThing:

                //获取需要搜索的歌曲名字
                String content = search_content.getText().toString().trim();
                if (content != "" && content != null) {
                    Toast.makeText(this, "开始查询", Toast.LENGTH_LONG).show();
                    //查询数据库是否有该歌曲（模糊搜索）
                    OkhttpDownload.searchSongs(content, new SetMusicListListener() {
                                @Override
                                public void setMusicList(List<Music> musicList) {
                                    //通知uiHandler更新界面
                                    Message message = new Message();
                                    message.what = 0;
                                    message.obj = musicList;
                                    uiHandler.sendMessage(message);
                                }
                            }
                    );
                    //查询该歌手的歌曲
                    OkhttpDownload.searchSongsFromSinger(content, new SetMusicListListener() {
                        @Override
                        public void setMusicList(List<Music> musicList) {
                            //通知uiHandler更新界面
                            Message message = new Message();
                            message.what = 1;
                            message.obj = musicList;
                            uiHandler.sendMessage(message);
                        }
                    });
                } else {
                    Toast.makeText(this, "搜索不能为空", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }
}
