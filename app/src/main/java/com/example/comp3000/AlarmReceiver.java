package com.example.comp3000;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;

//note: for testing purposes alarm may go off up to 2 mins late before code changes need reverting
public class AlarmReceiver extends BroadcastReceiver {

    public static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm fired!");

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

            //android studio doesn't like this, as long as the user gets the perm dialogue it's OK
            NotificationManagerCompat.from(context).notify(1, builder.build());

            //start playing CC alarm sound
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }).start();
    }
}