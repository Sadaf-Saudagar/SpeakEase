package com.example.speakease;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class GreetingActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_greeting);

        tts = new TextToSpeech(this, this);

        // Back Button
        findViewById(R.id.btnBackGreetings).setOnClickListener(v -> finish());

        // Mapping All TTS Buttons (Formal)
        setupTtsButton(R.id.ttsFormal1, R.id.textFormal1);
        setupTtsButton(R.id.ttsFormal2, R.id.textFormal2);
        setupTtsButton(R.id.ttsFormal3, R.id.textFormal3);
        setupTtsButton(R.id.ttsFormal4, R.id.textFormal4);
        setupTtsButton(R.id.ttsFormal5, R.id.textFormal5);

        // Mapping All TTS Buttons (Informal)
        setupTtsButton(R.id.ttsInformal1, R.id.textInformal1);
        setupTtsButton(R.id.ttsInformal2, R.id.textInformal2);
        setupTtsButton(R.id.ttsInformal3, R.id.textInformal3);
        setupTtsButton(R.id.ttsInformal4, R.id.textInformal4);
        setupTtsButton(R.id.ttsInformal5, R.id.textInformal5);
    }

    private void setupTtsButton(int buttonId, int textId) {
        ImageButton btn = findViewById(buttonId);
        TextView tv = findViewById(textId);
        if (btn != null && tv != null) {
            btn.setOnClickListener(v -> speak(tv.getText().toString()));
        }
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "GreetingTTS");
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}