package com.example.houshuai.bindserviceplaymusic;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressbar;
    private LocalBroadcastManager lbm;
    private MyBindServer nowServiceSelf;
    private Intent intent;
    private MyServerConnection myServerConnection;
    private MyResver myResver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化progressbar
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
    //激活服务
        intent = new Intent(this, MyBindServer.class);
        lbm = LocalBroadcastManager.getInstance(this);
        myResver = new MyResver();
        IntentFilter intentFilter = new IntentFilter("a.mp.total");
        lbm.registerReceiver(myResver,intentFilter);
        startService(intent);
        myServerConnection = new MyServerConnection();
        bindService(intent, myServerConnection, BIND_AUTO_CREATE);

    }
    public  void start(View view) {
        switch (view.getId()) {
            //开始
            case R.id.start:
                int nowProcess = nowServiceSelf.getNowProcess();
                nowServiceSelf.play(nowProcess);
                break;
            //暂停
            case R.id.stop:
                nowServiceSelf.pause();
                break;
            //重置
            case R.id.restart:
                nowServiceSelf.reStart();
                break;

        }
    }

    private class MyServerConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBindServer.MyIBinder   binder = (MyBindServer.MyIBinder) service;

            nowServiceSelf = binder.getNowServiceSelf();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
    private class MyResver  extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("a.mp.total".equals(intent.getAction())) {
                int totalProgress = intent.getIntExtra("totalProgress",-1);
                if (totalProgress > 0) {

                    progressbar.setMax(totalProgress);
                }
                int nowProgress = intent.getIntExtra("nowProgress", -1);
                if (nowProgress > 0) {
                    progressbar.setProgress(nowProgress);
                }
                if (progressbar.getProgress()==progressbar.getMax()) {
                    unbindService(myServerConnection);
                    lbm.unregisterReceiver(myResver);
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
