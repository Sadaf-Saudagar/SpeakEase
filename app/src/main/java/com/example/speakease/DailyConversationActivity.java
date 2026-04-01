package com.example.speakease;

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class DailyConversationActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private TextView tvLocation;
    private TextView[] tvTexts, tvLabels;
    private int lastPlaceIdx = -1;

    private String[] places = {"Restaurant", "Shopping", "Hospital", "Airport", "Park"};

    class Sentence {
        String text, type, formality;
        Sentence(String t, String ty, String f) {
            text = t; type = ty; formality = f;
        }
    }

    private ArrayList<ArrayList<Sentence>> masterList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_conversation);

        tvLocation = findViewById(R.id.tvCurrentLocation);

        // Map the 5 Fixed XML Cards
        tvTexts = new TextView[]{
                findViewById(R.id.text1), findViewById(R.id.text2),
                findViewById(R.id.text3), findViewById(R.id.text4),
                findViewById(R.id.text5)
        };

        tvLabels = new TextView[]{
                findViewById(R.id.label1), findViewById(R.id.label2),
                findViewById(R.id.label3), findViewById(R.id.label4),
                findViewById(R.id.label5)
        };

        setupDatabase();
        tts = new TextToSpeech(this, this);

        findViewById(R.id.btnBackConv).setOnClickListener(v -> finish());
        findViewById(R.id.btnShuffle).setOnClickListener(v -> shuffleData());

        setupTTSButtons();
        // shuffleData() will be called once TTS is initialized in onInit
    }

    private void setupTTSButtons() {
        int[] ttsIds = {R.id.tts1, R.id.tts2, R.id.tts3, R.id.tts4, R.id.tts5};
        for (int i = 0; i < ttsIds.length; i++) {
            final int index = i;
            findViewById(ttsIds[i]).setOnClickListener(v -> {
                String text = tvTexts[index].getText().toString();
                if (!text.isEmpty()) {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "CONV_TTS");
                }
            });
        }
    }

    private void shuffleData() {
        int placeIdx;
        // Prevent showing the same place twice in a row
        do {
            placeIdx = new Random().nextInt(places.length);
        } while (placeIdx == lastPlaceIdx);
        lastPlaceIdx = placeIdx;

        // Pick Formality
        boolean isFormal = new Random().nextBoolean();
        String formalityStr = isFormal ? "Formal" : "Informal";
        int color = isFormal ? Color.parseColor("#4A65FF") : Color.parseColor("#FF9800");

        tvLocation.setText("Place: " + places[placeIdx]);

        // Filter sentences for the current place and chosen formality
        ArrayList<Sentence> filtered = new ArrayList<>();
        for (Sentence s : masterList.get(placeIdx)) {
            if (s.formality.equals(formalityStr)) {
                filtered.add(s);
            }
        }

        // Update the 5 fixed cards with the new data
        for (int i = 0; i < 5; i++) {
            if (i < filtered.size()) {
                Sentence s = filtered.get(i);
                tvTexts[i].setText(s.text);
                tvLabels[i].setText(s.type + " | " + s.formality + " | " + places[placeIdx]);
                tvLabels[i].setBackgroundColor(color);
            }
        }
    }

    private void setupDatabase() {
        for (int i = 0; i < 5; i++) masterList.add(new ArrayList<>());

        // RESTAURANT (Index 0)
        masterList.get(0).add(new Sentence("I would like to reserve a table.", "Assertive", "Formal"));
        masterList.get(0).add(new Sentence("May I see the wine list?", "Interrogative", "Formal"));
        masterList.get(0).add(new Sentence("Please bring me the check.", "Imperative", "Formal"));
        masterList.get(0).add(new Sentence("What an exquisite atmosphere!", "Exclamatory", "Formal"));
        masterList.get(0).add(new Sentence("May you enjoy your meal.", "Optative", "Formal"));
        // Slang
        masterList.get(0).add(new Sentence("This food is bussin', no cap!", "Assertive", "Informal"));
        masterList.get(0).add(new Sentence("Yo, where's the menu?", "Interrogative", "Informal"));
        masterList.get(0).add(new Sentence("Gimme the bill, fam.", "Imperative", "Informal"));
        masterList.get(0).add(new Sentence("Sheesh, this place is fire!", "Exclamatory", "Informal"));
        masterList.get(0).add(new Sentence("Hope you eat well, bestie.", "Optative", "Informal"));

        // SHOPPING (Index 1)
        masterList.get(1).add(new Sentence("I am looking for formal attire.", "Assertive", "Formal"));
        masterList.get(1).add(new Sentence("Do you accept credit cards?", "Interrogative", "Formal"));
        masterList.get(1).add(new Sentence("Kindly show me your latest collection.", "Imperative", "Formal"));
        masterList.get(1).add(new Sentence("What a remarkable variety!", "Exclamatory", "Formal"));
        masterList.get(1).add(new Sentence("May you find the perfect gift.", "Optative", "Formal"));
        // Slang
        masterList.get(1).add(new Sentence("This fit is total drip.", "Assertive", "Informal"));
        masterList.get(1).add(new Sentence("How much is this? Don't gatekeep.", "Interrogative", "Informal"));
        masterList.get(1).add(new Sentence("Slide that discount my way.", "Imperative", "Informal"));
        masterList.get(1).add(new Sentence("No way, this price is a steal!", "Exclamatory", "Informal"));
        masterList.get(1).add(new Sentence("Manifesting a great sale for you.", "Optative", "Informal"));

        // HOSPITAL (Index 2)
        masterList.get(2).add(new Sentence("I require a medical consultation.", "Assertive", "Formal"));
        masterList.get(2).add(new Sentence("What are the side effects of this medication?", "Interrogative", "Formal"));
        masterList.get(2).add(new Sentence("Please rest and stay hydrated.", "Imperative", "Formal"));
        masterList.get(2).add(new Sentence("What a remarkable recovery you have made!", "Exclamatory", "Formal"));
        masterList.get(2).add(new Sentence("May you achieve a full and speedy recovery.", "Optative", "Formal"));
        // Slang
        masterList.get(2).add(new Sentence("I'm really not having a good time, chief.", "Assertive", "Informal"));
        masterList.get(2).add(new Sentence("Is this medicine actually effective or just mid?", "Interrogative", "Informal"));
        masterList.get(2).add(new Sentence("Take a chill pill and drink some water, bestie.", "Imperative", "Informal"));
        masterList.get(2).add(new Sentence("Sheesh, your glow-up after surgery is real!", "Exclamatory", "Informal"));
        masterList.get(2).add(new Sentence("Manifesting 100% health for you, no cap.", "Optative", "Informal"));

        // AIRPORT (Index 3)
        masterList.get(3).add(new Sentence("The flight departure has been delayed.", "Assertive", "Formal"));
        masterList.get(3).add(new Sentence("Could you direct me to the international terminal?", "Interrogative", "Formal"));
        masterList.get(3).add(new Sentence("Please fasten your seatbelts for takeoff.", "Imperative", "Formal"));
        masterList.get(3).add(new Sentence("How magnificent the view is from this altitude!", "Exclamatory", "Formal"));
        masterList.get(3).add(new Sentence("May your journey be pleasant and safe.", "Optative", "Formal"));
        // Slang
        masterList.get(3).add(new Sentence("The airline totally ghosted us on this flight.", "Assertive", "Informal"));
        masterList.get(3).add(new Sentence("Where's the gate? Don't leave me hanging.", "Interrogative", "Informal"));
        masterList.get(3).add(new Sentence("Buckle up, we're about to send it!", "Imperative", "Informal"));
        masterList.get(3).add(new Sentence("No way, this view is main-character energy!", "Exclamatory", "Informal"));
        masterList.get(3).add(new Sentence("Sending good vibes for your trip—travel safe!", "Optative", "Informal"));

        // PARK (Index 4)
        masterList.get(4).add(new Sentence("The botanical garden is exceptionally well maintained.", "Assertive", "Formal"));
        masterList.get(4).add(new Sentence("Are there any public facilities nearby?", "Interrogative", "Formal"));
        masterList.get(4).add(new Sentence("Kindly dispose of your litter in the bins.", "Imperative", "Formal"));
        masterList.get(4).add(new Sentence("What a splendid sunset over the horizon!", "Exclamatory", "Formal"));
        masterList.get(4).add(new Sentence("May you find tranquility in nature.", "Optative", "Formal"));
        // Slang
        masterList.get(4).add(new Sentence("This park is a whole vibe, honestly.", "Assertive", "Informal"));
        masterList.get(4).add(new Sentence("Yo, is there a place to charge my phone?", "Interrogative", "Informal"));
        masterList.get(4).add(new Sentence("Don't be trashy—pick up your trash.", "Imperative", "Informal"));
        masterList.get(4).add(new Sentence("The sunset is giving... it's literally perfect!", "Exclamatory", "Informal"));
        masterList.get(4).add(new Sentence("Hope this park trip heals your soul, for real.", "Optative", "Informal"));

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.ENGLISH);
            shuffleData(); // Load first data only after TTS is ready
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