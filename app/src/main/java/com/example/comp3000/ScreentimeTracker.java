package com.example.comp3000;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreentimeTracker extends BroadcastReceiver {
    public static long lastScreenOffTime = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            lastScreenOffTime = System.currentTimeMillis();
        }
    }
}