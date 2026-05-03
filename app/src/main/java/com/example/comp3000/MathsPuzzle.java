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

public class MathsPuzzle extends AppCompatActivity{
    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmTimePicker = findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        /*
        TextView changingText = findViewById(R.id.textView);
        changingText.setText("woah!");
        */

        EditText input = findViewById(R.id.editText);
        TextView modifyText = findViewById(R.id.textView);
        Button submit = findViewById(R.id.button);

        submit.setOnClickListener(v -> {
            String s = input.getText().toString();
            modifyText.setText(s);
        });
    }

    //Alarm on/off button
    public void OnToggleClicked(View view) {
        long time;
        if (((ToggleButton) view).isChecked()) {
            Toast.makeText(MathsPuzzle.this, "ALARM ON", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE);

            //set AM/PM
            time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
            if (System.currentTimeMillis() > time) {
                if (Calendar.AM_PM == 0)
                    time = time + (1000 * 60 * 60 * 12);
                else
                    time = time + (1000 * 60 * 60 * 24);
            }
            //continuous ringing alarm - the user need to toggle the alarm off with the button in app for now
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 60000, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(MathsPuzzle.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }
    }

}