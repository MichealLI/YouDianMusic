package com.example.ming.youdianmusic.common;

/**
 * 歌曲信息类
 * Created by Ming on 2017/6/1.
 */

public class MusicInfo {
    private String name;   //歌曲名字
    private String path;  // 歌曲路径
    private String duration;//长度
    private int time; //总时长
    private String singer; //歌手名字

    public String getLyricPath() {
        return lyricPath;
    }

    public void setLyricPath(String lyricPath) {
        this.lyricPath = lyricPath;
    }

    public String getMusicPicPath() {
        return musicPicPath;
    }

    public void setMusicPicPath(String musicPicPath) {
        this.musicPicPath = musicPicPath;
    }

    private String lyricPath; //歌词路径
    private String musicPicPath; //专辑图片路径

    public MusicInfo(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


}
