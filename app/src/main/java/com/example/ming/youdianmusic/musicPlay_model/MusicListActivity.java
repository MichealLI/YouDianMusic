package com.example.ming.youdianmusic.musicPlay_model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ming.youdianmusic.MyApp;
import com.example.ming.youdianmusic.R;
import com.example.ming.youdianmusic.common.MusicInfo;
import com.example.ming.youdianmusic.common.MyBinder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MusicListActivity extends Activity implements View.OnClickListener {
    private ImageView m_pre;
    private ImageView m_startPause;
    private ImageView m_next;
    private TextView songName;
    private SeekBar seekBar;
    private TextView now_time;
    private TextView time;
    private String max_time;
    private LinearLayout bottomPlay;


    private ListView musicListView;  //本地歌单
    private ArrayList<MusicInfo> musicInfosList; //保存歌曲的集合
    private MyApp myApp;
    private MyBinder myBinder;
    private MusicInfo musicInfo;


    private boolean isStart = true;
    private int currentIndex = 0;

    //用来重复同一操作
    private Handler mHandler = new Handler();

    private Runnable music_run = new Runnable() {
        @Override
        public void run() {
            // 执行重复的操作
            // 进度条不停地动，音乐时间不停的动
//            if(myBinder.isPlaying()){
                seekBar.setProgress(myBinder.getCurrentPosition());
                // 文本（当前时间）
                String now = secondMillesToString(myBinder.getCurrentPosition());
                now_time.setText(now);
                if (max_time.equals(now)) {
                    playMusic(2);
                }
                //播放歌曲发送了变化
                if (currentIndex != myApp.index ) {
                    // 设置歌名
                    songName.setText(musicInfosList.get(myApp.index).getName());
                    // 设置时长
                    max_time = musicInfosList.get(myApp.index).getDuration();
                    time.setText(max_time);
                    // 设置进度条的最大值
                    seekBar.setMax(myBinder.getDuration());
                    m_startPause.setImageResource(R.drawable.bpause);
                }


                // 设置执行的时间间隔
                mHandler.postDelayed(music_run, 1000);// 每隔一秒执行一次music_run中的run方法

            }
//            }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music_list);


        //获取本地歌曲列表
        myApp = (MyApp) getApplication();
        musicInfosList = myApp.getMusicInfoList();
        Log.e("Ming", "musicInfosList:" + musicInfosList.size());
        myBinder = myApp.getMyBinder();

        //初始化控件
        initView();

        //初始化Bottom部分
        // 设置歌名
        songName.setText(musicInfosList.get(myApp.index).getName());
        // 设置时长
        max_time = musicInfosList.get(myApp.index).getDuration();
        time.setText(max_time);
        // 设置进度条的最大值
        seekBar.setMax(myBinder.getDuration());

        //启动Handler，不断监听进度条和时间的变化
        mHandler.removeCallbacks(music_run);
        mHandler.post(music_run);


        //配置适配器
        MusicListAdapter musicListAdapter = new MusicListAdapter(this, musicInfosList);
        musicListView.setAdapter(musicListAdapter);
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(MusicListActivity.this, MusicPlayerActivity.class);
                myApp.index = position;
                intent.putExtra("position", position);
                intent.putExtra("isLocal",true);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        musicListView = (ListView) findViewById(R.id.music_list);
        m_pre = (ImageView) findViewById(R.id.m_pre);
        m_next = (ImageView) findViewById(R.id.m_next);
        m_startPause = (ImageView) findViewById(R.id.m_pause);
        songName = (TextView) findViewById(R.id.songName);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        now_time = (TextView) findViewById(R.id.m_now_time);
        time = (TextView) findViewById(R.id.m_time);
        bottomPlay = (LinearLayout) findViewById(R.id.bottom_play);

        if(myBinder.isPlaying()){
            m_startPause.setImageResource(R.drawable.bpause);
        }else{
            m_startPause.setImageResource(R.drawable.bstart);
        }


        m_pre.setOnClickListener(this);
        m_next.setOnClickListener(this);
        m_startPause.setOnClickListener(this);
        bottomPlay.setOnClickListener(this);


        //设置自己可以拖动进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int max = seekBar.getMax();
                    int temp = seekBar.getProgress();
                    musicInfo = musicInfosList.get(myApp.index);
                    int duration = musicInfo.getTime();
                    int now_duration = (int) (duration * (temp * 1.0 / max));
                    String now = secondMillesToString(now_duration);
                    now_time.setText(now);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myBinder.isPlaying()) {
                    myBinder.seekTo(seekBar.getProgress());
                } else {
                    m_startPause
                            .setImageResource(R.drawable.bpause);
                    myBinder.seekTo(seekBar.getProgress());
                    myBinder.continueMusic();

                }
            }
        });
    }


    /**
     * 播放音乐： 1.启动播放 2.点击播放按钮播放 3.点击下一曲播放 4.点击上一曲播放
     */
    private void playMusic(int i) {

        switch (i) {
            case 0:// 启动播放

                break;
            case 1:// 上一曲
                myApp.index--;
                if (myApp.index < 0) {
                    myApp.index = musicInfosList.size() - 1;
                }
                break;
            case 2:// 下一曲
                myApp.index++;
                if (myApp.index > musicInfosList.size() - 1) {
                    myApp.index = 0;
                }
                break;
            case 3:// 点击播放

                break;
            default:
                break;
        }
        if (myBinder != null) {
            if (musicInfosList.size() > 0) {
                currentIndex = myApp.index;
                // 开始播放
                myBinder.startMusic(musicInfosList.get(myApp.index).getPath());
                // 设置歌名
                songName.setText(musicInfosList.get(myApp.index).getName());
                // 设置时长
                max_time = musicInfosList.get(myApp.index).getDuration();
                time.setText(max_time);
                // 设置进度条的最大值
                seekBar.setMax(myBinder.getDuration());
                m_startPause.setImageResource(R.drawable.bpause);
            }
        }
    }

    /**
     * 将毫秒转化为固定格式的时间 1. java simpleDateFormat 2. DecimalFormat
     */
    private String secondMillesToString(int mills) {
        int seconds = mills / 1000;
        // 不足一分钟的秒数
        int second = seconds % 60;
        int munis = (seconds - second) / 60;
        // 格式类
        DecimalFormat df = new DecimalFormat("00");
        return df.format(munis) + ":" + df.format(second);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.m_pre:
                playMusic(1);
                break;
            case R.id.m_next:
                playMusic(2);
                break;
            case R.id.m_pause:
                if (myBinder.isPlaying()) {
                    myBinder.pauseMusic();
                    m_startPause
                            .setImageResource(R.drawable.bstart);
                } else {
                    if (isStart) {
                        playMusic(0);
                        isStart = false;
                    } else {
                        myBinder.continueMusic();
                    }
                    m_startPause
                            .setImageResource(R.drawable.bpause);
                }

                break;
            case R.id.bottom_play:
                Intent intent4 = new Intent(this, MusicPlayerActivity.class);
                intent4.putExtra("position", myApp.index);
                intent4.putExtra("isLocal",true);
                startActivity(intent4);
                break;

        }
    }
}
