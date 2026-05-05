package com.example.comp3000;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ScreenStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ScreenStateReceiver", "Screen state changed: " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            SharedPreferences prefs = context.getSharedPreferences("screen", Context.MODE_PRIVATE);
            prefs.edit().putLong("lastScreenOn", System.currentTimeMillis()).apply();
            Log.d("ScreenStateReceiver", "lastScreenOn saved");
        }
    }
}