package com.example.ming.youdianmusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ming.youdianmusic.common.MusicInfo;
import com.example.ming.youdianmusic.common.MyBinder;
import com.example.ming.youdianmusic.login_model.LoginActivity;
import com.example.ming.youdianmusic.musicDownload_model.DownLoadActivity;
import com.example.ming.youdianmusic.musicPlay_model.MusicListActivity;
import com.example.ming.youdianmusic.musicPlay_model.MusicPlayerActivity;
import com.example.ming.youdianmusic.musicSearch_model.SearchActivity;
import com.example.ming.youdianmusic.register_model.RegisterActivity;
import com.example.ming.youdianmusic.util.MenuAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private RelativeLayout local_music;
    private ImageView m_pre;
    private ImageView m_startPause;
    private ImageView m_next;
    private TextView songName;
    private SeekBar seekBar;
    private TextView now_time;
    private TextView time;
    private String max_time;
    private ImageView logo;
    private ImageView search;
    private DrawerLayout myDrawerLayout;
    private TextView musicNum;
    private RelativeLayout downLayout;
    private LinearLayout bottomPlay;
    private ImageView myApplogo;

    private ArrayList<MusicInfo> musicInfosList; //保存歌曲的集合
    private MyApp myApp;
    private TextView userName;
    private MyBinder myBinder = null;
    private MusicInfo musicInfo;
    private boolean isFirst = true;
    private boolean isStart = true;
    private int currentIndex; //当前歌曲下标

    private List<String> menuList = new ArrayList<String>();
    private ListView menuListView;

    //用来重复同一操作
    private Handler mHandler = new Handler();

    private Runnable music_run = new Runnable() {
        @Override
        public void run() {
            // 执行重复的操作
            // 进度条不停地动，音乐时间不停的动
            seekBar.setProgress(myBinder.getCurrentPosition());
            // 文本（当前时间）
            String now = secondMillesToString(myBinder.getCurrentPosition());
            now_time.setText(now);
            if (max_time.equals(now)) {
                playMusic(2);
            }
            //播放歌曲发送了变化
            if (currentIndex != myApp.index) {
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
    };

    //用来更新UI界面
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //初始化Bottom部分
                    // 设置歌名
                    songName.setText(musicInfosList.get(myApp.index).getName());
                    // 设置时长
                    max_time = musicInfosList.get(myApp.index).getDuration();
                    time.setText(max_time);
                    // 设置进度条的最大值
                    seekBar.setMax(myBinder.getDuration());
                    break;
                case 2:
                    myApplogo.setVisibility(View.GONE);
                    myDrawerLayout.setVisibility(View.VISIBLE);
                    myApp.isFirstLogin = false;
                    break;
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        //获取本地音乐集合
        myApp = (MyApp) getApplication();


        //初始化控件
        initView();

        //初始化菜单列表
        initMenuView();

        musicInfosList = myApp.getMusicInfoList();
        musicNum.setText(musicInfosList.size() + "首");

        //判断是否有用户登录
        if (myApp.isLogin) {
            userName.setText(myApp.userName);
        }

        //开启线程，直到音乐播放服务连接成功后执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (myApp.getMyBinder() != null) {
                        myBinder = myApp.getMyBinder();
                        Log.e("ming", "myBinder不为空");
                        //通知handler来更新UI界面
                        uiHandler.sendEmptyMessage(1);
                        break;
                    }
                }

            }
        }).start();

        if (myApp.isRight) {
            mHandler.removeCallbacks(music_run);
            mHandler.post(music_run);
        }
        uiHandler.sendEmptyMessageDelayed(2, 2000);
    }

    private void initMenuView() {
        //首先初始化数据
        menuList.add("登录");
        menuList.add("注册");
        menuList.add("注销");
        menuList.add("个人收藏");
        menuList.add("关于我们");
        //绑定控件
        menuListView = (ListView) findViewById(R.id.menu_list);
        //配置适配器
        MenuAdapter menuAdapter = new MenuAdapter(this, menuList);
        menuListView.setAdapter(menuAdapter);
    }


    /**
     * 初始化控件
     */
    private void initView() {

        local_music = (RelativeLayout) findViewById(R.id.local_music);
        m_pre = (ImageView) findViewById(R.id.m_pre);
        m_next = (ImageView) findViewById(R.id.m_next);
        m_startPause = (ImageView) findViewById(R.id.m_pause);
        songName = (TextView) findViewById(R.id.songName);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        now_time = (TextView) findViewById(R.id.m_now_time);
        time = (TextView) findViewById(R.id.m_time);
        logo = (ImageView) findViewById(R.id.logo);
        search = (ImageView) findViewById(R.id.search);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        downLayout = (RelativeLayout) findViewById(R.id.down_layout);
        musicNum = (TextView) findViewById(R.id.musicnum);
        bottomPlay = (LinearLayout) findViewById(R.id.bottom_play);
        menuListView = (ListView) findViewById(R.id.menu_list);
        userName = (TextView) findViewById(R.id.username);
        myApplogo = (ImageView) findViewById(R.id.myapp);

        if (!myApp.isFirstLogin) {
            myApplogo.setVisibility(View.GONE);
            myDrawerLayout.setVisibility(View.VISIBLE);
        }

        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (!myApp.isLogin) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "您已经登录了", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 1:
                        Intent intent1 = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        myApp.isLogin = false;
                        myApp.userName = null;
                        userName.setText("未登录");
                        break;
                }
            }
        });

        local_music.setOnClickListener(this);
        m_pre.setOnClickListener(this);
        m_next.setOnClickListener(this);
        m_startPause.setOnClickListener(this);
        logo.setOnClickListener(this);
        search.setOnClickListener(this);
        downLayout.setOnClickListener(this);
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
                    if (isStart) {
                        playMusic(0);
                        myBinder.seekTo(seekBar.getProgress());
                        isStart = false;
                    } else {
                        myBinder.seekTo(seekBar.getProgress());
                        myBinder.continueMusic();
                    }
                }
            }
        });
    }


    /**
     * 响应点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_music:
                Intent intent = new Intent(this, MusicListActivity.class);
                startActivity(intent);
                break;
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
            case R.id.logo:
                myDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.search:
                Intent intent2 = new Intent(this, SearchActivity.class);
                startActivity(intent2);
                break;
            case R.id.down_layout:
                Intent intent3 = new Intent(this, DownLoadActivity.class);
                startActivity(intent3);
                break;
            case R.id.bottom_play:
                Intent intent4 = new Intent(this, MusicPlayerActivity.class);
                intent4.putExtra("position", myApp.index);
                intent4.putExtra("isLocal", true);
                startActivity(intent4);
        }
    }

    /**
     * 播放音乐： 1.启动播放 2.点击播放按钮播放 3.点击下一曲播放 4.点击上一曲播放
     */
    private void playMusic(int i) {
        if (isFirst) {
            //启动Handler，不断监听进度条和时间的变化
            mHandler.removeCallbacks(music_run);
            mHandler.post(music_run);
            isFirst = false;
            myApp.isRight = true;
        }
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
}
