package com.example.houshuai.bindserviceplaymusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

/**
 * Created by HouShuai on 2016/6/6.
 */

public class MyBindServer extends Service {

    private LocalBroadcastManager lbm;
    private MediaPlayer mp;

    @Override
    public void onCreate() {
        //初始化广播接收着
        lbm = LocalBroadcastManager.getInstance(getApplicationContext());
        //出实话MediaPlayer
        mp = MediaPlayer.create(getApplicationContext(), R.raw.a);
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent("a.mp.total");
        intent.putExtra("totalProgress", mp.getDuration());
        lbm.sendBroadcast(intent);

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyIBinder();
    }

    public final class MyIBinder extends Binder {

        public MyBindServer getNowServiceSelf() {
            return MyBindServer.this;
        }
    }

    /*
    * 获得当前播放器的进度*/
    public int getNowProcess() {
        return mp.getCurrentPosition();
    }

    /*开始播放*/
    public void play(int nowProcess) {
        mp.seekTo(nowProcess);
        mp.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (;mp.isPlaying();) {
                    Intent intent = new Intent("a.mp.total");
                    intent.putExtra("nowProcess", mp.getCurrentPosition());
                    lbm.sendBroadcast(intent);
                }
            }
        }).start();


    }

    /*暂停播放*/
    public void pause() {
        mp.pause();
    }

    /*重置
    * */
    public void reStart() {
        try {
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }


}
