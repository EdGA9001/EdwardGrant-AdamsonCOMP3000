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
import androidx.core.app.ActivityCompat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    private long displayTime = -1;

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

    //Alarm on/off button
    public void OnToggleClicked(View view) {
        long time = -1;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
        time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));

        displayTime = time;
        String formattedTime = android.text.format.DateFormat.getTimeFormat(this).format(new Date(displayTime));

        if (((ToggleButton) view).isChecked()) {
            Toast.makeText(this, "ALARM ON: " + formattedTime, Toast.LENGTH_SHORT).show();
            if (System.currentTimeMillis() > time) time += 24 * 60 * 60 * 1000;
            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "ALARM OFF: " + formattedTime, Toast.LENGTH_SHORT).show();
            displayTime = -1;
        }
    }
}

/*old test code
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