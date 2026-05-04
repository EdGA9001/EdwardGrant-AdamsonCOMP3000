package com.example.comp3000;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.View;
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

public class MainActivity extends AppCompatActivity{
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    private long displayTime = -1;

    private final ArrayList<Long> alarmTimes = new ArrayList<>(Collections.singletonList(-1L));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        alarmTimePicker = findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        requestNotificationPermission();
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

        alarmTimes.add(0, time);
        if (alarmTimes.size() > 5) {
            alarmTimes.remove(5);
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

/*no longer needed to test alarms are being added
                TextView modifyText = findViewById(R.id.textView);
                modifyText.setText("");
                for (String timeStr : formattedTimes) {
                    modifyText.append(timeStr + "\n");
                }
*/

/*old threading
runOnUiThread(() -> {
        LinearLayout alarmList = findViewById(R.id.alarmListContainer);
        alarmList.removeAllViews();

        for (int i = 0; i < alarmTimes.size(); i++) {
            if (alarmTimes.get(i) != -1) {
                Button btn = new Button(this);
                btn.setText(formatAlarmTime(alarmTimes.get(i)));
                alarmList.addView(btn);
            }
        }
        });
        }).start();
 */

//TextView modifyText = findViewById(R.id.textView);
//modifyText.append(formattedTime + "\n");

/*removed alarm on/off in favour of simply adding alarms.
//Alarm on/off button
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

        alarmTimes.add(0, time);
        //for now limiting alarms at risk of overwhelming the UI for the user
        if (alarmTimes.size() > 5) {
            alarmTimes.remove(5);
        }
        String formattedTime = android.text.format.DateFormat.getTimeFormat(this).format(new Date(alarmTimes.get(0)));

        if (((ToggleButton) view).isChecked()) {
            showConfirmDialog(formattedTime, time, (ToggleButton) view);
        } else {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "ALARM OFF: " + formattedTime, Toast.LENGTH_SHORT).show();
            displayTime = -1;
        }
    }
 */

/*old way a method used to work:
    public void OnToggleClicked(View view) {
        long time = -1;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
        time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));

        alarmTimes.add(0, time);
        //for now limiting alarms at risk of overwhelming the UI for the user
        if (alarmTimes.size() > 5) {
            alarmTimes.remove(5);
        }
        String formattedTime = android.text.format.DateFormat.getTimeFormat(this).format(new Date(alarmTimes.get(0)));
        /*
        //displayTime = time;
        //String formattedTime = android.text.format.DateFormat.getTimeFormat(this).format(new Date(displayTime));

        TextView modifyText = findViewById(R.id.textView);
        Button submit = findViewById(R.id.toggleButton);

        submit.setOnClickListener(v -> modifyText.append(formattedTime + "\n"));


        if (((ToggleButton) view).isChecked()) {

Toast.makeText(this, "ALARM ON: " + formattedTime, Toast.LENGTH_SHORT).show();
if (System.currentTimeMillis() > time) time += 24 * 60 * 60 * 1000;
Intent intent = new Intent(this, AlarmReceiver.class);
pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
showConfirmDialog(formattedTime, time, (ToggleButton) view);
        } else {
        alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "ALARM OFF: " + formattedTime, Toast.LENGTH_SHORT).show();
displayTime = -1;
        }
        }
 */

/*old test code
TextView modifyText = findViewById(R.id.textView);
Button submit = findViewById(R.id.button);

submit.setOnClickListener(v -> modifyText.append(formattedTime + "\n"));

        //submit.setOnClickListener(v -> {
        //    String s = formattedTime;//input.getText().toString();
        //    modifyText.setText(s);
        //});

TextView changingText = findViewById(R.id.textView);
        changingText.setText("woah!");

        EditText input = findViewById(R.id.editText);
        TextView modifyText = findViewById(R.id.textView);
        Button submit = findViewById(R.id.button);

        submit.setOnClickListener(v -> {
            String s = input.getText().toString();
            modifyText.setText(s);
        });

        //continuous ringing alarm - the user need to toggle the alarm off with the button in app for now
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
 */