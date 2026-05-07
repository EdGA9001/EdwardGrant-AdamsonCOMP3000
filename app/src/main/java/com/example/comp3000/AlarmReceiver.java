package com.example.comp3000;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.lang.ref.WeakReference;

//note: for testing purposes alarm may go off 1 min late before code changes need reverting
public class AlarmReceiver extends BroadcastReceiver {
    public static MediaPlayer mediaPlayer;
    public static WeakReference<MainActivity> mainActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm fired!");

        //the values here are very short for demonstration purposes as a 6 hour demo of having a
        // users screen off before showing them they can wait an extra 90 minutes for an alarm
        // isn't exactly exciting or practical.
        long elapsed = System.currentTimeMillis() - ScreentimeTracker.lastScreenOffTime;
        ArduinoHeartReader.readHRValue();
        int hrValue = ArduinoHeartReader.recentHeartRate;

        if (elapsed < 10 * 1000 || hrValue > 80) {
            long newTime = System.currentTimeMillis() + (60 * 1000);
            Log.d("AlarmReceiver", "Recent screen or HR activity, rescheduling +" + newTime/60000 + " minutes");
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, newTime, pendingIntent);

            if (mainActivity.get() != null) {
                mainActivity.get().updateAlarmList(mainActivity.get().getFormattedTimes());
            }
            return;
        }

        else{
            Log.d("AlarmReceiver", "Did not reschedule alarm");
        }

        //if the screen wasn't on recently - proceeds as normal
        new Thread(() -> {
            NotificationChannel channel = new NotificationChannel("alarm_channel", "Alarm", NotificationManager.IMPORTANCE_HIGH);
            context.getSystemService(NotificationManager.class).createNotificationChannel(channel);

            Intent puzzleIntent = new Intent(context, PuzzlesActivity.class);
            puzzleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent puzzlePending = PendingIntent.getActivity(context, 0, puzzleIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm_channel")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Alarm!")
                    .setContentText("Wakey wakey!")
                    .addAction(0, "Dismiss", puzzlePending);

            NotificationManagerCompat.from(context).notify(1, builder.build());

            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }).start();
    }
}