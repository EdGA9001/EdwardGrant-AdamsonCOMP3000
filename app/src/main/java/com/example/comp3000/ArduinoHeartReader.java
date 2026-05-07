package com.example.comp3000;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ArduinoHeartReader {
    private static final String FILE_PATH = "C:\\Users\\admin\\Desktop\\COMP3000 Submission test\\sensor_value.txt";
    public static int recentHeartRate = 0;
    private static Timer timer;

    public static void startReading() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                readHRValue();
            }
        }, 0, 1000);
    }

    public static void stopReading() {
        if (timer != null) timer.cancel();
    }

    public static void readHRValue() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
            String value = reader.readLine();
            reader.close();
            recentHeartRate = Integer.parseInt(value.trim());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static String getHeartRateText() {
        if (recentHeartRate == 0) return "Heart Rate: No data yet";
        return "Heart Rate: " + recentHeartRate + " BPM";
    }
}
