package com.example.ming.youdianmusic.musicPlay_model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ming.youdianmusic.MyApp;
import com.example.ming.youdianmusic.R;
import com.example.ming.youdianmusic.common.Music;
import com.example.ming.youdianmusic.common.MusicInfo;
import com.example.ming.youdianmusic.common.MyBinder;
import com.example.ming.youdianmusic.util.DownImgListener;
import com.example.ming.youdianmusic.util.LyricView;
import com.example.ming.youdianmusic.util.OkhttpDownload;
import com.example.ming.youdianmusic.util.SetImgListener;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MusicPlayerActivity extends Activity implements View.OnClickListener {
    private TextView name;  //歌曲名字
    private TextView singerName; //歌手名字
    private Button pre;   //上一曲
    private Button next;  //下一曲
    private SeekBar seekBar; //进度条
    private Button start_pause; //开始或者暂停
    private TextView now_time; //播放时间
    private TextView time;   //播放总时长
    private String max_time; //播放总时长
    private Button back;  //返回键
    private ImageView singerImg; //专辑图片
    private LyricView lyricView;

    private int index; //记录需要播放的是第几首歌曲
    private ArrayList<MusicInfo> musicInfosList;  //保存传过来的歌单
    private MusicInfo musicInfo = null;
    private MyBinder myBinder;  //Service传过来的IBinder对象
    private Boolean isLocal;
    private MyApp myApp;

    //图片下载的地方
    public static final String DOWNLOADIMG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ydMusic/Img/";
    //歌词下载的地方
    public static final String DOWNLOADLyric_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ydMusic/Lyric/";
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
            if (max_time != null)
                if (max_time.equals(now)) {
                    playMusic(2);
                }

            // 设置执行的时间间隔
            mHandler.postDelayed(music_run, 1000);// 每隔一秒执行一次music_run中的run方法

        }
    };

    /**
     * 用来更新UI
     */
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //更新专辑图片
                    Bitmap bitmap = (Bitmap) msg.obj;
                    Log.e("ming", "更新专辑图片!");
                    singerImg.setImageBitmap(bitmap);
                    break;
                case 1:
                    //更新歌词
                    String lyricPath = (String) msg.obj;
                    Log.e("ming", "更新歌词地址！！！！！！" + lyricPath);
                    lyricView.setLrcPath(lyricPath);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music_player);

        Intent intent = getIntent();
        Boolean isLocal = intent.getBooleanExtra("isLocal", true);
        Music music = null;
        myApp = (MyApp) getApplication();
        if (isLocal) {
            index = intent.getIntExtra("position", 0);
        } else {
            music = (Music) intent.getSerializableExtra("music");
        }
        //获取本地音乐集合
        musicInfosList = myApp.getMusicInfoList();
        //获取当前歌曲
        musicInfo = musicInfosList.get(index);

        //初始化控件
        initView();

        //获取Application的音乐播放服务
        Log.e("Ming", "开始连接服务");

        myBinder = myApp.getMyBinder();


        //一启动就开始播放第index首歌

        if (!myBinder.isPlaying()) {
            // 改变播放按钮的状态
            start_pause
                    .setBackgroundResource(R.drawable.bpause);
            if (isLocal) {
                playMusic(0);
            } else {
                Log.e("ming", "开始播放网络音乐" + music.getMp3Url());
                myBinder.startMusic(music.getMp3Url());
                name.setText(music.getMusicName());
                singerName.setText(music.getArtistName());
                int length = myBinder.getDuration();
                Log.e("ming", "length:" + length);
                max_time = secondMillesToString(length);
                time.setText(max_time);
                seekBar.setMax(length);
            }

        } else {
            if (isLocal) {
                playMusic(0);
            } else {
                myBinder.startMusic(music.getMp3Url());
                name.setText(music.getMusicName());
                singerName.setText(music.getArtistName());
                int length = myBinder.getDuration();
                max_time = secondMillesToString(length);
                time.setText(max_time);
                seekBar.setMax(length);
            }

        }


        //启动Handler，不断监听进度条和时间的变化
        mHandler.removeCallbacks(music_run);
        mHandler.post(music_run);

        //创建前台通知
        creatNotificaion();


    }

    /**
     * 创建前台通知
     */

    private void creatNotificaion() {
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(R.drawable.icon,"酷猫", System.currentTimeMillis());
//        Intent intent = new Intent(this,MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        notification.setLatestEventInfo(this, musicInfo.getName(), musicInfo.getSinger(), pendingIntent);
//        notificationManager.notify(1, notification);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        name = (TextView) findViewById(R.id.name);
        singerName = (TextView) findViewById(R.id.singerName);
        pre = (Button) findViewById(R.id.pre);
        next = (Button) findViewById(R.id.next);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        start_pause = (Button) findViewById(R.id.start);
        now_time = (TextView) findViewById(R.id.now_time);
        time = (TextView) findViewById(R.id.time);
        back = (Button) findViewById(R.id.back);
        singerImg = (ImageView) findViewById(R.id.singerImg);
        //设计动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        singerImg.startAnimation(animation);

        lyricView = (LyricView) findViewById(R.id.lyricData);


        //绑定监听事件
        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        start_pause.setOnClickListener(this);
        back.setOnClickListener(this);

        //设置自己可以拖动进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int max = seekBar.getMax();
                    int temp = seekBar.getProgress();
                    musicInfo = musicInfosList.get(index);
                    int duration = musicInfo.getTime();
                    int now_duration = (int) (duration * (temp * 1.0 / max));
                    String now = secondMillesToString(now_duration);
                    now_time.setText(now);
                }
                Log.e("ming", "执行歌词绘制");
                lyricView.changeCurrent(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myBinder.isPlaying()) {
                    myBinder.seekTo(seekBar.getProgress());
                } else {
                    myBinder.seekTo(seekBar.getProgress());
                    start_pause
                            .setBackgroundResource(R.drawable.bpause);
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
                index--;
                if (index < 0) {
                    index = musicInfosList.size() - 1;
                }
                break;
            case 2:// 下一曲
                index++;
                if (index > musicInfosList.size() - 1) {
                    index = 0;
                }
                break;
            case 3:// 点击播放

                break;
            default:
                break;
        }
        singerImg.setImageResource(R.drawable.cartoon);
        lyricView.setLrcPath(DOWNLOADLyric_PATH+"test.lrc");
        if (myBinder != null) {
            if (musicInfosList.size() > 0) {
                musicInfo = musicInfosList.get(index);
                // 开始播放
                myBinder.startMusic(musicInfo.getPath());
                // 设置歌名
                Log.e("ming", "自然播放的第二首歌:" + musicInfo.getName());
                name.setText(musicInfo.getName());
                //设置歌手名字
                singerName.setText(musicInfo.getSinger());
                // 设置时长
                max_time = musicInfo.getDuration();
                time.setText(max_time);
                // 设置进度条的最大值
                seekBar.setMax(myBinder.getDuration());
                //判断是否有该歌曲的图片了，如果有的话，就直接加载，如果没有的话，就先网络下载，再显示
                if (musicInfo.getMusicPicPath() != null) {
                    Log.e("ming", "直接加载图片");
                    Bitmap bitmap = BitmapFactory.decodeFile(musicInfo.getMusicPicPath());
                    singerImg.setImageBitmap(bitmap);
                } else {
                    Log.e("ming", "下载图片");
                    //通过查询后台找到对应的URL
                    OkhttpDownload.searchSongData(musicInfo.getName(), musicInfo.getSinger(), new DownImgListener() {
                        @Override
                        public void downloadImg(String imgUrl, String lyricContent) {
                            //把URL传到Okhttp工具类，下载并加载显示图片
                            OkhttpDownload.downloadImg(imgUrl, musicInfo.getName(), new SetImgListener() {
                                @Override
                                public void setImg(Bitmap bitmap) {
                                    Message message = new Message();
                                    message.what = 0;
                                    message.obj = bitmap;
                                    musicInfo.setMusicPicPath(DOWNLOADIMG_PATH + musicInfo.getName() + ".jpg");
                                    uiHandler.sendMessage(message);
                                }
                            });
                        }
                    });

                }
                //判断是否有该歌曲的歌词了，如果有的话，就直接加载，如果没有的话，就先网络下载，再显示
                if (musicInfo.getLyricPath() != null) {
                    Log.e("ming", "直接显示歌词");
//                    File lyricFile = new File(musicInfo.getLyricPath());
//                    FileReader fr = null;
//                    StringBuffer sb = new StringBuffer();
//                    char temp[] = new char[1024];
//                    int len;
//                    try1 {
//                        fr = new FileReader(lyricFile);
//                        while ((len = fr.read(temp)) != -1) {
//                            String str = new String(temp, 0, len);
//                            sb.append(str);
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    lyricView.setLrcPath(musicInfo.getLyricPath());
                } else {
                    Log.e("ming", "下载歌词");
                    //通过查询后台找到对应的URL
                    OkhttpDownload.searchSongData(musicInfo.getName(), musicInfo.getSinger(), new DownImgListener() {
                        @Override
                        public void downloadImg(String imgUrl, String lyricContent) {
                            //把歌词写入文件中
                            File dirFile = new File(DOWNLOADLyric_PATH);
                            if (!dirFile.exists()) {
                                dirFile.mkdirs();
                            }
                            File lyricFile = new File(dirFile, musicInfo.getName() + ".lrc");
                            Log.e("ming", "路径！！！：" + lyricFile.getAbsolutePath());
                            FileOutputStream fos = null;
                            DataOutputStream dos = null;
                            try {
                                lyricFile.createNewFile();   //创建该文件
                                fos = new FileOutputStream(lyricFile);
                                //通过DataOutputStream流来写歌词
                                dos = new DataOutputStream(fos);
                                dos.writeUTF(lyricContent);
                                dos.flush();
                                Log.e("ming", "下载歌词成功");
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fos.close();
                                    dos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            musicInfo.setLyricPath(DOWNLOADLyric_PATH + musicInfo.getName() + ".lrc");
                            Message message = new Message();
                            message.what = 1;
                            message.obj = musicInfo.getLyricPath();
                            uiHandler.sendMessage(message);

                        }
                    });
                }
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


    /**
     * 响应点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //上一曲
            case R.id.pre:
                playMusic(1);
                break;
            //下一曲
            case R.id.next:
                playMusic(2);
                break;
            //开始或者暂停
            case R.id.start:
                if (myBinder.isPlaying()) {
                    myBinder.pauseMusic();
                    start_pause
                            .setBackgroundResource(R.drawable.bstart);
                } else {
                    myBinder.continueMusic();
                    start_pause
                            .setBackgroundResource(R.drawable.bpause);
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }

}
