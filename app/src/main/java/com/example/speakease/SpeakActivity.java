package com.example.speakease;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class SpeakActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    // UI Elements
    private TextView tvTargetSentence;
    private ImageButton btnPlayAudio, btnMicrophone, btnBack;
    private long startTime;
    private Button btnRandomSentence;
    private LineChart progressChart;

    // Logic Tools
    private TextToSpeech tts;
    private ActivityResultLauncher<Intent> sttLauncher;
    private DatabaseHelper dbHelper;

    private final String[] practiceSentences = {
            "I have a book.",
            "The weather is lovely today.",
            "Practice makes perfect.",
            "How do I get to the train station?",
            "I am learning English with SpeakEase.",
            "I hope we meet next time.",
            "Can you help me with this task?",
            "I enjoy listening to music.",
            "What time does the class start?",
            "She is reading a novel.",
            "We are going to the market.",
            "Please speak a little slower.",
            "I didn’t understand that.",
            "Could you repeat that, please?",
            "This is my favorite place.",
            "He works in a big company.",
            "I would like a cup of coffee.",
            "Let’s meet after lunch.",
            "Where are you from?",
            "I am feeling a bit tired today.",
            "That sounds like a great idea."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_speak);

        // 1. Initialize
        dbHelper = new DatabaseHelper(this);
        tvTargetSentence = findViewById(R.id.tvTargetSentence);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        btnMicrophone = findViewById(R.id.btnMicrophone);
        btnBack = findViewById(R.id.btnBack);
        btnRandomSentence = findViewById(R.id.btnRandomSentence);
        progressChart = findViewById(R.id.progressChart);
        EditText etCustomInput = findViewById(R.id.etCustomInput);
        ImageButton btnSpeakInput = findViewById(R.id.btnSpeakInput);
        Button btnSetAsTarget = findViewById(R.id.btnSetAsTarget);

        tts = new TextToSpeech(this, this);

        // 2. Setup Graph
        setupChartConfig();
        updateChartData();

        // 3. Speech to Text Result Handler
        sttLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (matches != null && !matches.isEmpty()) {
                            processResult(matches.get(0));
                        }
                    }
                }
        );

        // 4. Click Listeners
        btnBack.setOnClickListener(v -> finish());

        btnRandomSentence.setOnClickListener(v -> {
            tvTargetSentence.setText(practiceSentences[new Random().nextInt(practiceSentences.length)]);
        });

        btnPlayAudio.setOnClickListener(v -> {
            tts.speak(tvTargetSentence.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, "TTS_ID");
        });

        btnMicrophone.setOnClickListener(v -> {
            startTime = System.currentTimeMillis();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak clearly now...");
            sttLauncher.launch(intent);
        });

        btnSpeakInput.setOnClickListener(v -> {
            String text = etCustomInput.getText().toString();
            if (!text.isEmpty()) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "Custom_TTS");
            } else {
                Toast.makeText(this, "Type something first!", Toast.LENGTH_SHORT).show();
            }
        });

// 2. Lock it in as the practice goal
        btnSetAsTarget.setOnClickListener(v -> {
            String text = etCustomInput.getText().toString();
            if (!text.isEmpty()) {
                tvTargetSentence.setText(text); // Moves typed text to the main target display
                Toast.makeText(this, "Target Updated!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int calculateFluency(String spoken, long timeTakenMillis) {
        if (spoken.isEmpty()) return 0;

        double seconds = timeTakenMillis / 1000.0;
        int wordCount = spoken.split("\\s+").length;

        // Average speaking rate is about 130-150 words per minute (WPM)
        double wpm = (wordCount / seconds) * 60;

        // Logic: If WPM is between 100 and 160, you get a high fluency score.
        // If you are too slow (under 60 WPM) or too fast/mumbly, the score drops.
        int fluencyScore;
        if (wpm > 100 && wpm < 170) {
            fluencyScore = 90 + (new Random().nextInt(10)); // 90-100%
        } else if (wpm >= 60) {
            fluencyScore = 70 + (new Random().nextInt(20)); // 70-89%
        } else {
            fluencyScore = 40 + (new Random().nextInt(30)); // 40-70%
        }

        return Math.min(fluencyScore, 100); // Cap at 100%
    }

    private void processResult(String spokenText) {
        long timeTaken = System.currentTimeMillis() - startTime;
        String targetText = tvTargetSentence.getText().toString();

        int accuracy = calculateAccuracy(targetText, spokenText);

        // Real dynamic fluency!
        int fluency = calculateFluency(spokenText, timeTaken);

        // Pronunciation is usually Accuracy + a bit of "confidence" jitter
        int pronunciation = Math.min(accuracy + (new Random().nextInt(5)), 100);

        // Save to DB and show Dialog
        dbHelper.insertScore(accuracy);
        updateChartData();
        showFeedbackDialog(spokenText, targetText, accuracy > 90, accuracy, pronunciation, fluency);
    }

    private void showFeedbackDialog(String spoken, String target, boolean isCorrect, int acc, int pron, int flu) {
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_feedback, null);
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Initialize Dialog Views
        TextView tvSpoken = dialogView.findViewById(R.id.diagSpokenText);
        TextView tvCorrectMark = dialogView.findViewById(R.id.diagCorrectMark);
        TextView tvCorrection = dialogView.findViewById(R.id.diagCorrection);
        TextView tvFluent = dialogView.findViewById(R.id.diagFluentSuggestion);
        TextView tvAcc = dialogView.findViewById(R.id.diagAccScore);
        TextView tvPron = dialogView.findViewById(R.id.diagPronScore);
        TextView tvFlu = dialogView.findViewById(R.id.diagFluScore);
        Button btnClose = dialogView.findViewById(R.id.btnCloseDialog);


        // Set Data
        tvSpoken.setText("Sentence: " + spoken);
        tvCorrectMark.setText("Correct: " + (isCorrect ? "✅" : "❌"));
        tvCorrection.setText("Correction: " + target);
        tvFluent.setText(isCorrect? "Good Job! No Suggestions":"Try listening to the sentence you typed"); // This can be dynamic later using an API

        tvAcc.setText(acc + "%");
        tvPron.setText(pron + "%");
        tvFlu.setText(flu + "%");

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private int calculateAccuracy(String target, String spoken) {
        String s1 = target.toLowerCase().replaceAll("\\p{Punct}", "").trim();
        String s2 = spoken.toLowerCase().replaceAll("\\p{Punct}", "").trim();
        if (s1.equals(s2)) return 100;

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (s1.charAt(i - 1) != s2.charAt(j - 1))
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return (int) ((1.0 - ((double) costs[s2.length()] / Math.max(s1.length(), s2.length()))) * 100);
    }

    private void setupChartConfig() {
        progressChart.getDescription().setEnabled(false);
        progressChart.setTouchEnabled(true);
        progressChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        progressChart.getAxisRight().setEnabled(false);
        progressChart.getAxisLeft().setAxisMaximum(100f);
        progressChart.getAxisLeft().setAxisMinimum(0f);
    }

    private void updateChartData() {
        ArrayList<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT score FROM progress ORDER BY id DESC LIMIT 10", null);

        int index = 0;
        if (cursor.moveToLast()) { // Plot chronologically
            do {
                entries.add(new Entry(index++, cursor.getInt(0)));
            } while (cursor.moveToPrevious());
        }
        cursor.close();

        LineDataSet set = new LineDataSet(entries, "Accuracy Trend");
        set.setColor(Color.BLUE);
        set.setCircleColor(Color.BLUE);
        set.setLineWidth(2f);
        set.setValueTextSize(10f);

        LineData data = new LineData(set);
        progressChart.setData(data);
        progressChart.invalidate();
    }

    @Override public void onInit(int status) { if (status == TextToSpeech.SUCCESS) tts.setLanguage(Locale.UK); }

    @Override protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}