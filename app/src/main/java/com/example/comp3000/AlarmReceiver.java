package com.example.comp3000;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.content.SharedPreferences;
import java.util.Calendar;

//for debugging may have to wait 30+seconds to confirm alarm isn't working before going wild fixing it
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm fired!");

        NotificationChannel channel = new NotificationChannel("alarm_channel", "Alarm", NotificationManager.IMPORTANCE_HIGH);
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Alarm!")
                .setContentText("Wakey wakey!");

        Log.d("AlarmReceiver", "Notification should have been received");
        NotificationManagerCompat.from(context).notify(1, builder.build());
    }
}