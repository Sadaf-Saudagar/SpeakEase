package com.example.speakease;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide ActionBar for the full-screen dashboard look
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        // 1. Initialize the primary button from your Hero Card
        Button btnSpeak = findViewById(R.id.btnSpeak);



        // 2. Set the Intent to go to SpeakActivity
        btnSpeak.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SpeakActivity.class);
            startActivity(intent);
        });

        TextView startGreetings = findViewById(R.id.startLearning1);
        startGreetings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GreetingActivity.class);
            startActivity(intent);
        });

        TextView startConv = findViewById(R.id.startLearning2);
        startConv.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DailyConversationActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.startLearning3).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PronunciationTipsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.startGrammar).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GrammarBasicsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.startVocabulary).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VocabActivity.class);
            startActivity(intent);
        });

        TextView btnOpenQna = findViewById(R.id.btnOpenQna);
        btnOpenQna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the Intent to switch screens
                Intent intent = new Intent(MainActivity.this, QNAActivity.class);
                startActivity(intent);
            }
        });
    }


}