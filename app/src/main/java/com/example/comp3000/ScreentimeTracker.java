package com.example.comp3000;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class ScreentimeTracker extends BroadcastReceiver {
    public static long lastScreenOffTime = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ScreentimeTracker", "Received: " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            lastScreenOffTime = System.currentTimeMillis();
        }
    }

    public static void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(new ScreentimeTracker(), filter);
    }

    public static String getScreenTimeText() {
        if (lastScreenOffTime == 0) return "Recent screen activity: No data yet";
        long elapsed = System.currentTimeMillis() - lastScreenOffTime;
        long seconds = elapsed / (1000);
        return "Screen was last on " + seconds + " seconds ago";
    }
}