package com.example.comp3000;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

//trialing services to replace convoluted alarm system
//please refactor other components like this
//in built commands also neaten things up a lot
public class AlarmService extends Service {
    public static MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}