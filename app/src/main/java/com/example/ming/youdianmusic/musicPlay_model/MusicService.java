package com.example.ming.youdianmusic.musicPlay_model;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.ming.youdianmusic.common.MyBinder;


/**
 * Created by Ming on 2017/6/3.
 */

public class MusicService extends Service {
    private MyBinder myBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

}

