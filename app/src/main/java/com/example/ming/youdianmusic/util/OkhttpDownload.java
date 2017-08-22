package com.example.ming.youdianmusic.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.ming.youdianmusic.common.Music;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 使用OkHttp框架来下载歌词，歌曲专辑图片
 * Created by Ming on 2017/6/4.
 */

public class OkhttpDownload {
    //1.获取okHttpClient对象
    private static OkHttpClient okHttpClient = new OkHttpClient();

    //图片下载的地方
    public static final String DOWNLOADIMG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ydMusic/Img/";

    //歌词下载的地方
    public static final String DOWNLOADLyric_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ydMusic/Lyric/";

    //数据库歌曲查询地址
    public static final String DATA_PATH = "http://192.168.235.21:8080/YDMusic/MusicServlet?sn=";

    /**
     * 下载网络图片
     */
    public static void downloadImg(String url, final String name, final SetImgListener setImgListener) {
        //2.构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        //3.将Request封装成call
        Call call = okHttpClient.newCall(request);
        //执行call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.e("ming", "下载成功");
                    byte[] data = response.body().bytes();
                    Bitmap imgBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    //把图片写入文件中
                    File dirFile = new File(DOWNLOADIMG_PATH);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    File imgFile = new File(dirFile, name + ".jpg");
                    Log.e("ming", imgFile.getAbsolutePath());
                    imgFile.createNewFile();   //创建该文件
                    //通过文件流来保存图片
                    FileOutputStream fos = new FileOutputStream(imgFile);
                    imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                    //接口回调方式显示图片
                    setImgListener.setImg(imgBitmap);
                }
            }
        });
    }


    /**
     * 通过歌曲名字和歌手名来查询数据库中对应的Json数据,并返回歌词信息和图片的URL
     */
    public static void searchSongData(String name, String singer, final DownImgListener downImgListener) {
        String url = DATA_PATH + name + "&an=" + singer;
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String jsonData = response.body().string();
                //解析json数据
                List<Music> musicList = parserJsonData(jsonData);
                //获取专辑图片和歌词的url
                if (musicList.size() >= 1) {
                    String imgUrl = musicList.get(0).getMusicPic();
                    String lyricContent = musicList.get(0).getLyric();
                    Log.e("lyricContent:", lyricContent);
                    //通过接口回调来进行下一步
                    downImgListener.downloadImg(imgUrl, lyricContent);

                }
            }
        });

    }

    /**
     * 通过歌曲名字来查询数据库中对应的Json数据
     */
    public static void searchSongs(String name, final SetMusicListListener setMusicListListener) {
        String url = DATA_PATH + name;
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    //解析json数据
                    List<Music> musicList = parserJsonData(jsonData);
                    //获取专辑图片和歌词的url
                    Log.e("ming", "查询成功"+musicList.size());
                    if (musicList.size() >= 1) {
                        String imgUrl = musicList.get(0).getMusicPic();
                        String lyricContent = musicList.get(0).getLyric();
                        Log.e("lyricContent:", lyricContent);
                        //通过接口回调来进行下一步
                        setMusicListListener.setMusicList(musicList);
                    }
                }

            }
        });
    }

    /**
     *通过歌手来搜索歌曲
     */
    public static void searchSongsFromSinger(String singerName, final SetMusicListListener setMusicListListener) {
        String url = DATA_PATH  + "&an=" + singerName;
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    //解析json数据
                    List<Music> musicList = parserJsonData(jsonData);
                    //获取专辑图片和歌词的url
                    Log.e("ming", "查询成功"+musicList.size());
                    if (musicList.size() >= 1) {
                        String imgUrl = musicList.get(0).getMusicPic();
                        String lyricContent = musicList.get(0).getLyric();
                        Log.e("lyricContent:", lyricContent);
                        //通过接口回调来进行下一步
                        setMusicListListener.setMusicList(musicList);
                    }
                }
            }
        });
    }


    /**
     * 解析Json数据
     */
    private static List parserJsonData(String jsonData) {
        Gson gson = new Gson();
        List<Music> musicList = gson.fromJson(jsonData, new TypeToken<List<Music>>() {
        }.getType());
        return musicList;
    }

}

