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
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Runnable updateRunnable;

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

    public static void startPeriodicUpdates(Runnable refreshCallback) {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                refreshCallback.run();
                handler.postDelayed(this, 2 * 60 * 1000);
            }
        };
        handler.post(updateRunnable);
    }

    public static void stopPeriodicUpdates() {
        if (updateRunnable != null) handler.removeCallbacks(updateRunnable);
    }

    public static String getScreenTimeText() {
        if (lastScreenOffTime == 0) return "Recent screen activity: No data yet";
        long elapsed = System.currentTimeMillis() - lastScreenOffTime;
        long seconds = elapsed / (1000);
        return "Screen was last on " + seconds + " seconds ago";
    }
}