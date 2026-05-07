package com.example.comp3000;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ArduinoHeartReader {
    public static int recentHeartRate = 0;
    private static ScheduledExecutorService executor;

    public static void startReading(Context context) {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> readHRValue(context), 0, 1, TimeUnit.SECONDS);
    }

    public static void stopReading() {
        if (executor != null) executor.shutdown();
    }

    public static void readHRValue(Context context) {
        try {
            URL url = new URL("http://10.0.2.2:8000/heartrate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String value = reader.readLine();
            reader.close();
            recentHeartRate = Integer.parseInt(value.trim());
        } catch (Exception e) {
            Log.e("ArduinoHeartReader", "Error fetching HR: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

        public static String getHeartRateText() {
        if (recentHeartRate == 0) return "Heart Rate: No data yet";
        return "Heart Rate: " + recentHeartRate + " BPM";
    }
}