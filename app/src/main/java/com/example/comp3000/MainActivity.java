package com.example.comp3000;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.Calendar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.content.ContextCompat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import androidx.appcompat.app.AlertDialog;
import android.widget.LinearLayout;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.content.Context;

public class MainActivity extends AppCompatActivity{
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    private final ArrayList<Long> alarmTimes = new ArrayList<>(Collections.singletonList(-1L));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        alarmTimePicker = findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        SharedPreferences prefs = getSharedPreferences("alarms", MODE_PRIVATE);
        long savedTime = prefs.getLong("alarmTime", -1);
        if (savedTime != -1) {
            alarmTimes.add(0, savedTime);
            updateAlarmList(new ArrayList<>(Collections.singletonList(formatAlarmTime(savedTime))));
        }

        requestNotificationPermission();
        updateScreenTime();
        ScreentimeTracker.registerReceiver(this);
        ScreentimeTracker.lastScreenOffTime = System.currentTimeMillis() - (1000);
        updateScreenTime();
    }

    private void updateScreenTime() {
        LinearLayout screenText = findViewById(R.id.recentScreentimeDisplay);
        TextView textView = (TextView) screenText.getChildAt(1);
        textView.setText(ScreentimeTracker.getScreenTimeText());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateScreenTime();
    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission", "Notification permission granted");
        }
    }

    private String formatAlarmTime(long alarmTimeMillis) {
        return android.text.format.DateFormat.getTimeFormat(this).format(new Date(alarmTimeMillis));
    }

    public void OnToggleClicked(View view) {
        long time = -1;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
        time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));

        if (alarmTimes.contains(time)) {
            Toast.makeText(this, "Alarm already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        //works with limit >1 but this is a dynamic alarm
        //Hence the goal is to make 1 alarm work where 5 would fail for now
        alarmTimes.add(0, time);
        if (alarmTimes.size() > 1) {
            alarmTimes.remove(1);
        }
        String formattedTime = formatAlarmTime(time);
        showConfirmDialog(formattedTime, time, null);
    }

    private void showConfirmDialog(String formattedTime, long time, ToggleButton btn) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Alarm")
                .setMessage("Set alarm for " + formattedTime + "?")
                .setPositiveButton("Confirm", (d, w) -> setAlarm(formattedTime, time))
                .setNegativeButton("Cancel", (d, w) -> btn.setChecked(false))
                .show();
    }

    private void setAlarm(String formattedTime, long time) {
        Toast.makeText(this, "ALARM ON: " + formattedTime, Toast.LENGTH_SHORT).show();
        if (System.currentTimeMillis() > time) time += 24 * 60 * 60 * 1000;
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, (int) time, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        SharedPreferences prefs = getSharedPreferences("alarms", MODE_PRIVATE);
        prefs.edit().putLong("alarmTime", time).apply();

        new Thread(() -> {
            ArrayList<String> formattedTimes = new ArrayList<>();
            for (long alarmTime : alarmTimes) {
                if (alarmTime != -1) {
                    formattedTimes.add(formatAlarmTime(alarmTime));
                }
            }

            runOnUiThread(() -> {
                updateAlarmList(formattedTimes);
            });
        }).start();
    }
    private void updateAlarmList(ArrayList<String> formattedTimes) {
        LinearLayout alarmList = findViewById(R.id.alarmListContainer);
        alarmList.removeAllViews();
        for (String timeStr : formattedTimes) {
            Button btn = new Button(this);
            btn.setText(timeStr);
            alarmList.addView(btn);
        }
    }
}