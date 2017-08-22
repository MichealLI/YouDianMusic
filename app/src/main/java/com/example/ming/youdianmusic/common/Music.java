package com.example.ming.youdianmusic.common;

import java.io.Serializable;

/**
 * Created by Ming on 2017/6/4.
 */

public class Music implements Serializable{
    private String musicId;
    private String lyric;
    private String mp3Url;
    private String musicPic;
    private String artistName;
    private String musicName;

    public Music() {
        super();
    }
    public Music(String musicId, String lyric, String mp3Url, String musicPic,
                 String artistName, String musicName) {
        super();
        this.musicId = musicId;
        this.lyric = lyric;
        this.mp3Url = mp3Url;
        this.musicPic = musicPic;
        this.artistName = artistName;
        this.musicName = musicName;
    }
    public String getArtistName() {
        return artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    public String getMusicName() {
        return musicName;
    }
    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
    public String getMusicId() {
        return musicId;
    }
    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }
    public String getLyric() {
        return lyric;
    }
    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
    public String getMp3Url() {
        return mp3Url;
    }
    public void setMp3Url(String mp3Url) {
        this.mp3Url = mp3Url;
    }
    public String getMusicPic() {
        return musicPic;
    }
    public void setMusicPic(String musicPic) {
        this.musicPic = musicPic;
    }
}
