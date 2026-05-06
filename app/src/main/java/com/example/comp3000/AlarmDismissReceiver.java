package com.example.comp3000;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AlarmDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //check for puzzle completion
        SharedPreferences prefs = context.getSharedPreferences("puzzle", Context.MODE_PRIVATE);
        boolean puzzleCompleted = prefs.getBoolean("puzzleCompleted", false);

        //once I've actually implemented a puzzle submit button this needs to be a while AFTER opening puzzle activity
        if (puzzleCompleted) {
            //dismisses notification
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.cancel(1);
        }

        //open PuzzlesActivity
        Intent puzzleIntent = new Intent(context, PuzzlesActivity.class);
        puzzleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(puzzleIntent);
    }
}