package com.dreamobyfire.plugin;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class BackgroundTaskService extends Service {
    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss:SSS");

    private DataCallback dataCallback = null;
    private PowerManager.WakeLock wakeLock = null;

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onCreate() {
        System.out.println("onCreate");
        super.onCreate();
        Toast.makeText(this, "BasckgroundTaskService start", Toast.LENGTH_SHORT).show();

        /**
         * 设置电源模式为保持CPU运行
         */
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BackgroundTaskService.class.getName());
        wakeLock.acquire();

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        BroadcastReceiver receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                    dataCallback.onDataChange("android.media.VOLUME_CHANGED_ACTION");
                } else {
                    dataCallback.onDataChange(intent.getAction());
                }

            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(receiver, intentFilter);

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");
        Toast.makeText(this, "BasckgroundTaskService start", Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("---onUnbind---");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "BasckgroundTaskService stop", Toast.LENGTH_SHORT).show();

        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e("onTaskRemoved", "<<<onTaskRemoved>>>");
        System.out.println("<<<onTaskRemoved>>>");
    }

    public void doNotifyEmit(String text) {
//        socket.open();
    }

    public class Binder extends android.os.Binder {
        public BackgroundTaskService getService() {
            return BackgroundTaskService.this;
        }
    }

    public void setDataCallback(DataCallback dataCallback) {
        this.dataCallback = dataCallback;
    }

    public static interface DataCallback {
        void onDataChange(String data);
    }
}
