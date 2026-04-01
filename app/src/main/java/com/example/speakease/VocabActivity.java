package com.example.speakease;

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VocabActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VocabAdapter adapter;
    private LinearLayout categoryContainer;
    private EditText etSearch;
    private TextToSpeech tts;

    private Map<String, List<VocabModel>> database = new HashMap<>();
    private List<VocabModel> currentList = new ArrayList<>();
    private Button lastSelectedButton = null;

    public static class VocabModel {
        public String word, type, meaning, synonym, example;
        public VocabModel(String w, String t, String m, String s, String e) {
            this.word = w; this.type = t; this.meaning = m; this.synonym = s; this.example = e;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocab);

        // 1. Setup TTS
        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) tts.setLanguage(Locale.US);
        });

        // 2. Initialize Views
        recyclerView = findViewById(R.id.rvVocabCards);
        categoryContainer = findViewById(R.id.vocabCategoryContainer);
        etSearch = findViewById(R.id.etSearchVocab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Setup Logic
        setupDatabase();
        createCategoryButtons();

        // 4. Search Filter Logic
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) adapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Default Load
        loadCategory("Essential Idioms", (Button) categoryContainer.getChildAt(0));

        findViewById(R.id.btnBackVocab).setOnClickListener(v -> finish());
    }

    private void setupDatabase() {

        // 1. ESSENTIAL IDIOMS
        List<VocabModel> idioms = new ArrayList<>();
        idioms.add(new VocabModel("Break the ice", "Phrase", "To initiate conversation in a social setting.", "Start a conversation", "He told a joke to break the ice."));
        idioms.add(new VocabModel("Piece of cake", "Phrase", "Something that is very easy to accomplish.", "Very easy", "The exam was a piece of cake."));
        idioms.add(new VocabModel("Under the weather", "Phrase", "Feeling slightly ill or unwell.", "Unwell", "I'm feeling a bit under the weather today."));
        idioms.add(new VocabModel("Call it a day", "Phrase", "To stop working on something for the rest of the day.", "Stop working", "We’ve done enough; let's call it a day."));
        idioms.add(new VocabModel("Hit the nail on the head", "Phrase", "To describe exactly what is causing a situation.", "Be exactly right", "You hit the nail on the head with your explanation."));
        idioms.add(new VocabModel("Burn the midnight oil", "Phrase", "To work late into the night.", "Work late", "She burned the midnight oil to finish the assignment."));
        idioms.add(new VocabModel("Spill the beans", "Phrase", "To reveal a secret unintentionally.", "Reveal a secret", "He spilled the beans about the surprise party."));
        database.put("Essential Idioms", idioms);

        // 2. PROFESSIONAL
        List<VocabModel> work = new ArrayList<>();
        work.add(new VocabModel("Collaborate", "Verb", "To work jointly with others to achieve a common goal.", "Work together", "We need to collaborate on this project."));
        work.add(new VocabModel("Leverage", "Verb", "To use something to maximum advantage.", "Utilize effectively", "We should leverage our social media presence."));
        work.add(new VocabModel("Deadline", "Noun", "The latest time by which a task must be completed.", "Due date", "The deadline for the report is Friday."));
        work.add(new VocabModel("Facilitate", "Verb", "To make a process easier or more efficient.", "Make easier", "The new software will facilitate faster billing."));
        work.add(new VocabModel("Delegate", "Verb", "To assign responsibility or tasks to others.", "Assign tasks", "Managers should delegate responsibilities effectively."));
        work.add(new VocabModel("Optimize", "Verb", "To make something as effective or functional as possible.", "Improve efficiency", "We need to optimize the system performance."));
        work.add(new VocabModel("Initiative", "Noun", "The ability to assess and act independently.", "Self-starting ability", "She showed great initiative in solving the issue."));
        database.put("Professional", work);

        // 3. EMOTIONS
        List<VocabModel> emotions = new ArrayList<>();
        emotions.add(new VocabModel("Ecstatic", "Adjective", "Feeling overwhelming happiness or joy.", "Overjoyed", "She was ecstatic about her new job."));
        emotions.add(new VocabModel("Apprehensive", "Adjective", "Anxious or fearful that something bad may happen.", "Worried", "I'm apprehensive about the results."));
        emotions.add(new VocabModel("Exasperated", "Adjective", "Extremely irritated or frustrated.", "Frustrated", "He gave an exasperated sigh at the delay."));
        emotions.add(new VocabModel("Ambivalent", "Adjective", "Having mixed or conflicting feelings.", "Uncertain", "I feel ambivalent about moving to a new city."));
        emotions.add(new VocabModel("Resilient", "Adjective", "Able to recover quickly from difficulties.", "Emotionally strong", "Children are often very resilient."));
        emotions.add(new VocabModel("Skeptical", "Adjective", "Doubtful or not easily convinced.", "Doubtful", "I am skeptical about his promises."));
        emotions.add(new VocabModel("Elated", "Adjective", "Extremely happy and excited.", "Thrilled", "He felt elated after hearing the good news."));
        emotions.add(new VocabModel("Melancholy", "Adjective", "A deep, reflective sadness.", "Sad", "She felt a sense of melancholy on rainy days."));
        emotions.add(new VocabModel("Anxious", "Adjective", "Experiencing worry or unease.", "Nervous", "He was anxious before the interview."));
        database.put("Emotions", emotions);

        // 4. TRAVEL & TRANSIT
        List<VocabModel> travel = new ArrayList<>();
        travel.add(new VocabModel("Itinerary", "Noun", "A planned route or schedule of a journey.", "Travel plan", "Check the itinerary for flight times."));
        travel.add(new VocabModel("Jet lag", "Noun", "Fatigue caused by long-distance travel across time zones.", "Travel fatigue", "I need sleep to recover from jet lag."));
        travel.add(new VocabModel("Layover", "Noun", "A waiting period between connecting flights.", "Stopover", "I have a six-hour layover in Dubai."));
        travel.add(new VocabModel("Amenities", "Noun", "Features that provide comfort or convenience.", "Facilities", "The hotel has excellent amenities, including a gym."));
        travel.add(new VocabModel("Boarding pass", "Noun", "A pass allowing a passenger to board an aircraft.", "Flight ticket", "Please present your boarding pass at the gate."));
        travel.add(new VocabModel("Customs", "Noun", "The official department that regulates goods entering a country.", "Border control", "We had to go through customs after landing."));
        travel.add(new VocabModel("Reservation", "Noun", "An arrangement to secure accommodation or a service in advance.", "Booking", "I made a hotel reservation online."));
        database.put("Travel", travel);

        // 5. DAILY LIFE
        List<VocabModel> daily = new ArrayList<>();
        daily.add(new VocabModel("Commute", "Verb", "To travel regularly between home and work.", "Travel daily", "I commute to the city by train."));
        daily.add(new VocabModel("Errand", "Noun", "A short trip undertaken to complete a task.", "Small task", "I have a few errands to run today."));
        daily.add(new VocabModel("Procrastinate", "Verb", "To delay or postpone tasks unnecessarily.", "Delay", "Don't procrastinate on your homework!"));
        daily.add(new VocabModel("Inevitably", "Adverb", "In a manner that is certain to happen.", "Unavoidably", "Inevitably, it started raining during the picnic."));
        daily.add(new VocabModel("Routine", "Noun", "A regular sequence of actions.", "Habit", "I follow a strict morning routine."));
        daily.add(new VocabModel("Groceries", "Noun", "Food and household items purchased regularly.", "Food items", "I need to buy groceries for the week."));
        daily.add(new VocabModel("Chores", "Noun", "Regular household tasks.", "Housework", "I finished all my chores before dinner."));
        database.put("Daily Life", daily);

    }

    private void createCategoryButtons() {
        String[] topics = {"Essential Idioms", "Professional", "Emotions", "Travel", "Daily Life"};
        for (String topic : topics) {
            Button btn = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.setMargins(0, 0, 16, 0);
            btn.setLayoutParams(params);
            btn.setText(topic);
            btn.setAllCaps(false);
            btn.setPadding(40, 20, 40, 20);
            btn.setBackgroundResource(R.drawable.btn_outline_pill);
            btn.setOnClickListener(v -> loadCategory(topic, btn));
            categoryContainer.addView(btn);
        }
    }

    private void loadCategory(String category, Button clickedBtn) {
        if (lastSelectedButton != null) {
            lastSelectedButton.setBackgroundResource(R.drawable.btn_outline_pill);
            lastSelectedButton.setTextColor(Color.BLACK);
        }
        clickedBtn.setBackgroundResource(R.drawable.btn_solid_pill);
        clickedBtn.setTextColor(Color.WHITE);
        lastSelectedButton = clickedBtn;

        currentList = database.get(category);
        if (currentList == null) currentList = new ArrayList<>();

        adapter = new VocabAdapter(currentList, word ->
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "VOCAB_TTS"));
        recyclerView.setAdapter(adapter);
        etSearch.setText(""); // Clear search when switching categories
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}