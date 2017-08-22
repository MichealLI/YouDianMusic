package com.example.ming.youdianmusic.common;

import android.media.MediaPlayer;
import android.os.Binder;

import java.io.IOException;

/**
 * Created by Ming on 2017/6/4.
 */

public class MyBinder extends Binder {
    private MediaPlayer mediaPlayer = null;

    public MyBinder() {
        mediaPlayer = new MediaPlayer();
    }

    /**
     * 开始播放
     */
    public void startMusic(String mPath) {
        //1.重置
        mediaPlayer.reset();
        //2.根据路径加载文件
        try {
            mediaPlayer.setDataSource(mPath);
            //3.缓存音频文件
            mediaPlayer.prepare();
            //4.监听是否缓存成功，若成功则开始播放
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //开始播放
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放
     */
    public void pauseMusic() {
        //如果在播放的话，则暂停
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    /**
     * 继续播放
     */
    public void continueMusic() {
        if (!mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    /**
     * 判断是否正在播放
     */
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * 获取歌曲的总时长
     */
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * 跳到指定的地方播放
     */
    public void seekTo(int process) {
        mediaPlayer.seekTo(process);
    }

    /**
     * 获取当前播放位置
     */
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
}
