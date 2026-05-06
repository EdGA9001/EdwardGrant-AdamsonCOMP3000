package com.example.comp3000;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class AlarmDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //puzzle completion check
        SharedPreferences prefs = context.getSharedPreferences("puzzle", Context.MODE_PRIVATE);
        boolean puzzleCompleted = prefs.getBoolean("puzzleCompleted", false);

        //checks puzzle completion then removes notification and alarm sound
        if (puzzleCompleted) {
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.cancel(1);

            AlarmReceiver.mediaPlayer.stop();
            AlarmReceiver.mediaPlayer.release();
            AlarmReceiver.mediaPlayer = null;
        }

        //open PuzzlesActivity
        Intent puzzleIntent = new Intent(context, PuzzlesActivity.class);
        puzzleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(puzzleIntent);
    }
}