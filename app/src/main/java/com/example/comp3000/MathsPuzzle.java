package com.example.comp3000;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

public class MathsPuzzle extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}