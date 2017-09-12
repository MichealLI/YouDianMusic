package com.example.ming.youdianmusic;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.example.ming.youdianmusic.common.MusicInfo;
import com.example.ming.youdianmusic.common.MyBinder;
import com.example.ming.youdianmusic.musicPlay_model.MusicService;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Ming on 2017/6/4.
 */

public class MyApp extends Application {
    //创建一个音乐播放的服务
    private MusicService musicService;

    private ArrayList<MusicInfo> musicInfoList = new ArrayList<MusicInfo>(); //保存歌曲的集合

    public int index = 0;

    public boolean isRight = false;
    private MyBinder myBinder;  //Service传过来的IBinder对象

    public boolean isLogin = false;
    public boolean isFirstLogin = true;
    public String userName;

    //图片下载的地方
    public static final String DOWNLOADIMG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ydMusic/Img/";

    //歌词下载的地方
    public static final String DOWNLOADLyric_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ydMusic/Lyric/";

    //服务连接
    private ServiceConnection sConnection = new ServiceConnection() {
        /**
         *成功连接时调用
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("Ming", "服务连接成功");
            myBinder = (MyBinder) service;
        }

        /**
         *断开连接时调用
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //绑定服务
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        bindService(intent, sConnection, Context.BIND_AUTO_CREATE);

        //获取本地歌曲列表
        getLocalMusicList();
    }

    /**
     * 获取本地音乐
     */
    private void getLocalMusicList() {
        //通过ContentProvider来获取手机上的mp3文件
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        //通过遍历游标来遍历mp3文件
        while (cursor.moveToNext()) {
            //一首歌曲（程序中对应的是一个对象）
            MusicInfo musicInfo = new MusicInfo();
            //获取名字
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            if (!title.contains("-")) {
                musicInfo.setName(title);
                String singer = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                musicInfo.setSinger(singer);
            } else {
                String[] temp = title.split("-");
                String singer = temp[0].trim();
                String name = temp[1].trim();
                musicInfo.setSinger(singer);
                musicInfo.setName(name);
            }
            //获取路径
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            musicInfo.setPath(path);
            //获取的时长是毫秒
            int time = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            musicInfo.setTime(time);
            //把毫秒转化为标准时长，并转为String类型
            String duration = secondMilliesToString(time);
            musicInfo.setDuration(duration);

            //查询一下指定位置是否有相应的图片和歌词
            String img = searchImg(musicInfo.getName()); //查询图片
            if (img != null)
                musicInfo.setMusicPicPath(img);

            String lyric = searchLyric(musicInfo.getName()); //查询歌词
            if (lyric != null)
                Log.e("ming", "lyric:" + lyric);
            if (lyric != null)
                musicInfo.setLyricPath(lyric);

            //把该歌曲加到集合中去
            // 排除损坏的音频文件
            if (path != null && title != null && time != 0) {
                musicInfoList.add(musicInfo);
            }

        }
    }

    /**
     * 查询是否有该歌曲的图片
     */
    private String searchImg(String name) {
        File dirFile = new File(DOWNLOADIMG_PATH);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        for (String fileName : dirFile.list()) {
            if (fileName.equals(name + ".jpg")) {
                return DOWNLOADIMG_PATH + fileName;
            }
        }
        return null;
    }

    /**
     * 查选是否有该歌曲的歌词
     */
    private String searchLyric(String lyricName) {
        File dirFile = new File(DOWNLOADLyric_PATH);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        for (String fileName : dirFile.list()) {
//            Log.e("ming","fileName:  "+fileName + "   lyricName:"+lyricName + ".lrc");
            if (lyricName.equals("绅士"))
                Log.e("ming", lyricName + ".lrc");
            if (fileName.equals(lyricName + ".lrc")) {
                Log.e("ming", "找到歌词了");
                return DOWNLOADLyric_PATH + fileName;
            }
        }
        return null;
    }

    private String secondMilliesToString(int mills) {
        //将毫秒转换为固定格式的时间
        int seconds = mills / 1000;  //先把毫秒转为秒
        int second = seconds % 60;  //最终的秒数
        int minutes = (seconds - second) / 60;  //再把秒转为分
        DecimalFormat df = new DecimalFormat("00");
        return df.format(minutes) + ":" + df.format(second);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        //断开服务连接
        if (sConnection != null)
            unbindService(sConnection);
    }

    /**
     * 提供给外部的
     * 获取MyBinder对象
     *
     * @return
     */
    public MyBinder getMyBinder() {
        return myBinder;
    }

    /**
     * 获取本地音乐集合
     */
    public ArrayList<MusicInfo> getMusicInfoList() {
        return musicInfoList;
    }
}
