package com.example.speakease;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;

public class PronunciationTipsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private TextView tvTarget, tvResult, tvTag;
    private String targetPhrase = "";

    private ArrayList<PronunciationModel> tipList = new ArrayList<>();
    private int currentIdx = 0;

    // Model class for our tips
    class PronunciationModel {
        String soundTag, words, tip;
        PronunciationModel(String tag, String w, String t) {
            this.soundTag = tag;
            this.words = w;
            this.tip = t;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_pronunciation);


        if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        }

        // Initialize UI
        tvTarget = findViewById(R.id.tvTargetWords);
        tvResult = findViewById(R.id.tvResult);
        tvTag = findViewById(R.id.tvSoundTag);
        ImageButton btnHear = findViewById(R.id.btnHear);
        ImageButton btnMic = findViewById(R.id.btnMic);
        Button btnNext = findViewById(R.id.btnNextTip);
        findViewById(R.id.btnBackPron).setOnClickListener(v -> finish());

        setupTips();

        // 1. Setup TTS
        tts = new TextToSpeech(this, this);

        // 2. Setup Speech Recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);

        // Listeners
        btnHear.setOnClickListener(v -> tts.speak(targetPhrase, TextToSpeech.QUEUE_FLUSH, null, "PRON_TTS"));

        btnMic.setOnClickListener(v -> {
            tvResult.setText("Listening...");
            tvResult.setTextColor(Color.parseColor("#4A65FF"));
            speechRecognizer.startListening(speechIntent);
        });

        btnNext.setOnClickListener(v -> {
            currentIdx = (currentIdx + 1) % tipList.size();
            updateUI();
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    checkAccuracy(matches.get(0));
                }
            }

            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO: message = "Audio recording error"; break;
                    case SpeechRecognizer.ERROR_CLIENT: message = "Client side error"; break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: message = "Permission missing!"; break;
                    case SpeechRecognizer.ERROR_NETWORK: message = "Network error"; break;
                    case SpeechRecognizer.ERROR_NO_MATCH: message = "No speech detected"; break;
                    default: message = "Error: " + error; break;
                }
                tvResult.setText(message);
                tvResult.setTextColor(Color.RED);
            }

            // Required empty overrides
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });

        updateUI(); // Initial Load
    }

    private void setupTips() {

        tipList.add(new PronunciationModel(
                "TH Sound",
                "Think, Thank, Three",
                "Place the tip of your tongue between your upper and lower teeth, and gently blow air out."
        ));

        tipList.add(new PronunciationModel(
                "R Sound",
                "Red, Right, River",
                "Curl your tongue slightly backward without touching the roof of your mouth. Keep your lips slightly rounded."
        ));

        tipList.add(new PronunciationModel(
                "V vs W",
                "Vine, Wine, Vet, Wet",
                "For 'V', place your top teeth on your bottom lip and blow air. For 'W', round your lips into a small circle."
        ));

        tipList.add(new PronunciationModel(
                "L Sound",
                "Light, Lemon, Look",
                "Place the tip of your tongue against the ridge just behind your upper front teeth."
        ));

    }

    private void updateUI() {
        PronunciationModel current = tipList.get(currentIdx);
        targetPhrase = current.words;
        tvTag.setText(current.soundTag);
        tvTarget.setText(current.words);
        tvResult.setText("Tap Mic to practice this sound");
        tvResult.setTextColor(Color.GRAY);
    }

    private void checkAccuracy(String spoken) {
        String cleanTarget = targetPhrase.toLowerCase().replaceAll("[^a-zA-Z ]", "");
        String cleanSpoken = spoken.toLowerCase().replaceAll("[^a-zA-Z ]", "");

        // If the spoken text matches any word in our target
        if (cleanSpoken.contains(cleanTarget) || cleanTarget.contains(cleanSpoken)) {
            tvResult.setText("✅ Excellent! You said: \"" + spoken + "\"");
            tvResult.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            tvResult.setText("❌ Not quite. You said: \"" + spoken + "\"");
            tvResult.setTextColor(Color.RED);
            showRuntimeTip(tipList.get(currentIdx).tip);
        }
    }

    private void showRuntimeTip(String tipText) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Pronunciation Hint 💡")
                .setMessage(tipText)
                .setPositiveButton("Try Again", null)
                .show();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) tts.setLanguage(Locale.US);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        if (speechRecognizer != null) speechRecognizer.destroy();
        super.onDestroy();
    }
}