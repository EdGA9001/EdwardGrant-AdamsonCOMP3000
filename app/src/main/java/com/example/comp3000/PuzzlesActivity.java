package com.example.comp3000;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PuzzlesActivity extends AppCompatActivity {
    @androidx.camera.core.ExperimentalGetImage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzles);
        Button puzzleSubmit = findViewById(R.id.puzzleSubmit);
        puzzleSubmit.setOnClickListener(v -> completePuzzle());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 2);
        }

        PuzzleObjectDetector.start(this);

    }

    //checks if puzzle is completed, then stops the alarm
    private void completePuzzle() {
        Log.d("puzzleCompletionCheck", "submitted answer!");

        EditText answerInput = findViewById(R.id.answerInput);
        TextView title = findViewById(R.id.puzzlePageTitle);

        int answer = Integer.parseInt(answerInput.getText().toString());
        boolean correct = (answer == 144);

        title.setText(String.valueOf(correct));
        if (correct) {
            SharedPreferences prefs = getSharedPreferences("puzzle", MODE_PRIVATE);
            prefs.edit().putBoolean("puzzleCompleted", true).apply();

            if (AlarmReceiver.mediaPlayer != null) {
                AlarmReceiver.mediaPlayer.stop();
                AlarmReceiver.mediaPlayer.release();
            }
        }
    }
}