package com.example.comp3000;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PuzzlesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzles);
        Button puzzleSubmit = findViewById(R.id.puzzleSubmit);
        puzzleSubmit.setOnClickListener(v -> completePuzzle());
    }

    private void completePuzzle() {
        SharedPreferences prefs = getSharedPreferences("puzzle", MODE_PRIVATE);
        prefs.edit().putBoolean("puzzleCompleted", true).apply();
    }
}