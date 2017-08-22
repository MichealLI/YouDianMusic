package com.example.ming.youdianmusic.musicDownload_model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ming.youdianmusic.R;
import com.example.ming.youdianmusic.common.Music;
import com.example.ming.youdianmusic.util.OkhttpDownload;
import com.example.ming.youdianmusic.util.ProgressDownloader;
import com.example.ming.youdianmusic.util.ProgressResponseBody;
import com.example.ming.youdianmusic.util.SetImgListener;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;



public class DownLoadActivity extends Activity implements ProgressResponseBody.ProgressListener, View.OnClickListener {
    private SeekBar dSeekBar;
    private TextView dSongName;
    private Button continueD;
    private Button pauseD;
    private ListView downloadListView;
    private RelativeLayout downloadLayout;
    private Music music; //需要下载的歌曲信息

    //mp3下载的地方
    public static final String DOWNLOADMP3_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ydMusic/mp3/";
    //歌词下载的地方
    public static final String DOWNLOADLyric_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ydMusic/Lyric/";

    //保存已经下载的歌曲名字
    private List<String> downloadList  = new ArrayList<String>();
    private DownloadListAdapter adapter;

    private long breakPoints;
    private ProgressDownloader downloader;
    private File file;
    private long totalBytes;
    private long contentLength;


    //更新UI界面
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dSeekBar.setProgress(0);
            downloadLayout.setVisibility(View.INVISIBLE);
            Log.e("ming", "原来的downloadList:" + downloadList.size());
            downloadList.add(music.getMusicName()+".mp3");
            Log.e("ming", "现在的downloadList:" + downloadList.size());
            adapter.notifyDataSetChanged();
            downloadListView.invalidate();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        //初始化控件
        initView();

        //获取下载歌曲的信息
        Intent intent = getIntent();
        music = (Music) intent.getSerializableExtra("music");

        if (music != null) {
            //1.设置下载区域可见
            downloadLayout.setVisibility(View.VISIBLE);
            dSongName.setText(music.getMusicName());
            //2.通过mp3 URL来下载歌曲
            Log.e("ming", "mp3的地址：" + music.getMp3Url());
            downloadMusicMP3(music.getMp3Url(), music.getMusicName());
            //3.下载专辑图片
            OkhttpDownload.downloadImg(music.getMusicPic(), music.getMusicName(), new SetImgListener() {
                @Override
                public void setImg(Bitmap bitmap) {

                }
            });
            //4.下载歌词
            String lyricContent = music.getLyric();
            File lyricFile = new File(DOWNLOADLyric_PATH + music.getMusicName() + ".lrc");
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
        }

        //查询已经下载好的歌曲
        searchDownloadMusic();
        if (downloadList.size() >= 1) {
            //显示到downloadListView中去
            adapter = new DownloadListAdapter(this, downloadList);
            downloadListView.setAdapter(adapter);
        }

    }

    /**
     * 查询已经下载好的歌曲
     */
    private void searchDownloadMusic() {

        File dirFile = new File(DOWNLOADMP3_PATH);
        for (String name : dirFile.list()) {
            downloadList.add(name);
        }
    }

    private void initView() {
        dSeekBar = (SeekBar) findViewById(R.id.dseekBar);
        dSongName = (TextView) findViewById(R.id.downloadMusic);
        continueD = (Button) findViewById(R.id.continueD);
        pauseD = (Button) findViewById(R.id.pauseD);
        downloadListView = (ListView) findViewById(R.id.downloadList);
        downloadLayout = (RelativeLayout) findViewById(R.id.dowloadLayout);

        continueD.setOnClickListener(this);
        pauseD.setOnClickListener(this);

        
    }

    /**
     * 通过url来下载对应的mp3文件
     */
    private void downloadMusicMP3(String url, String name) {
        breakPoints = 0L;
        File dirFile = new File(DOWNLOADMP3_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        file = new File(dirFile, name + ".mp3");
        downloader = new ProgressDownloader(url, file, this);
        downloader.download(0L);
    }


    /**
     * 执行前的操作
     */
    @Override
    public void onPreExecute(long contentLength) {
        // 文件总长只需记录一次，要注意断点续传后的contentLength只是剩余部分的长度
        if (this.contentLength == 0L) {
            this.contentLength = contentLength;
            dSeekBar.setMax((int) (contentLength / 1024));
        }
    }

    /**
     * 更新进度条
     */
    @Override
    public void update(long totalBytes, boolean done) {
        // 注意加上断点的长度
        this.totalBytes = totalBytes + breakPoints;
        dSeekBar.setProgress((int) (totalBytes + breakPoints) / 1024);
        if (done) {
            uiHandler.sendEmptyMessage(0);
            // 切换到主线程
            Observable
                    .empty()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            Toast.makeText(DownLoadActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .subscribe();

        }
    }

    /**
     * 处理点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueD:
                //下载任务继续
                downloader.download(breakPoints);
                Toast.makeText(this, "下载继续", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pauseD:
                //下载任务暂停
                downloader.pause();
                Toast.makeText(this, "下载暂停", Toast.LENGTH_SHORT).show();
                // 存储此时的totalBytes，即断点位置。
                breakPoints = totalBytes;
                break;
        }
    }
}
