package com.example.speakease;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QNAActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QNAAdapter adapter;
    private LinearLayout categoryContainer;
    private EditText etAiPrompt;
    private ImageButton btnAskAi;
    private ProgressBar progressBar;

    private List<QNAModel> qnaList = new ArrayList<>();
    private Button lastSelectedButton = null;

    // The Data Model used by the Adapter
    public static class QNAModel {
        public String question, answerFormal, answerCasual, category;

        public QNAModel(String q, String af, String ac, String cat) {
            this.question = q;
            this.answerFormal = af;
            this.answerCasual = ac;
            this.category = cat;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

        // 1. Initialize UI Views
        recyclerView = findViewById(R.id.rvQnaCards);
        categoryContainer = findViewById(R.id.qnaCategoryContainer);
        etAiPrompt = findViewById(R.id.etAiPrompt);
        btnAskAi = findViewById(R.id.btnAskAi);
        progressBar = findViewById(R.id.pbAiLoading);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QNAAdapter(qnaList);
        recyclerView.setAdapter(adapter);

        // 2. Setup Category Buttons
        createCategoryButtons();

        // 3. Setup Default Data
        loadDefaultCategory("Daily Casual", (Button) categoryContainer.getChildAt(0));

        // 4. Live NVIDIA AI Integration
        btnAskAi.setOnClickListener(v -> {
            String prompt = etAiPrompt.getText().toString().trim();
            if (!prompt.isEmpty()) {
                askNvidiaAI(prompt);
            } else {
                Toast.makeText(this, "Please type a grammar question!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnBackQna).setOnClickListener(v -> finish());
    }

    private void createCategoryButtons() {
        String[] topics = {"Daily Casual", "Job Interview", "Travel"};
        for (String topic : topics) {
            Button btn = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.setMargins(0, 0, 16, 0);
            btn.setLayoutParams(params);
            btn.setText(topic);
            btn.setAllCaps(false);
            btn.setPadding(40, 20, 40, 20);
            btn.setBackgroundResource(R.drawable.btn_outline_pill);
            btn.setOnClickListener(v -> loadDefaultCategory(topic, btn));
            categoryContainer.addView(btn);
        }
    }

    private void loadDefaultCategory(String category, Button clickedBtn) {
        if (lastSelectedButton != null) {
            lastSelectedButton.setBackgroundResource(R.drawable.btn_outline_pill);
            lastSelectedButton.setTextColor(Color.BLACK);
        }
        clickedBtn.setBackgroundResource(R.drawable.btn_solid_pill);
        clickedBtn.setTextColor(Color.WHITE);
        lastSelectedButton = clickedBtn;

        qnaList.clear();

        if (category.equals("Daily Casual")) {
            qnaList.add(new QNAModel(
                    "How's it going today?",
                    "I am doing quite well, thank you.",
                    "Not bad! How about you?",
                    "Daily Casual"
            ));
            qnaList.add(new QNAModel(
                    "What are you up to?",
                    "I am currently occupied with my studies.",
                    "Just hanging out.",
                    "Daily Casual"
            ));
        } else if (category.equals("Job Interview")) {
            qnaList.add(new QNAModel(
                    "Why should we hire you?",
                    "I bring a strong combination of skills and discipline.",
                    "I work hard and learn quickly.",
                    "Job Interview"
            ));
        } else if (category.equals("Travel")) {
            qnaList.add(new QNAModel(
                    "Where is the station?",
                    "The station is two blocks to the east.",
                    "Just go two blocks that way.",
                    "Travel"
            ));
        }

        adapter.notifyDataSetChanged();
    }

    private void askNvidiaAI(String userQuestion) {
        progressBar.setVisibility(View.VISIBLE);
        btnAskAi.setEnabled(false);

        // NVIDIA API KEY!
        String apiKey = BuildConfig.BUILD_TYPE;

        List<NvidiaApiClient.Message> messages = new ArrayList<>();
        messages.add(new NvidiaApiClient.Message("system",
                "You are an English Grammar Tutor. Give a short, one-sentence formal answer and a short, one-sentence casual answer. Format strictly as follows: 'Formal: [text] | Casual: [text]'. Keep answers under 15 words."));
        messages.add(new NvidiaApiClient.Message("user", userQuestion));

        NvidiaApiClient.NvidiaRequest request = new NvidiaApiClient.NvidiaRequest("meta/llama-3.1-8b-instruct", messages);

        NvidiaApiClient.getService().getChatCompletion(apiKey, request).enqueue(new Callback<NvidiaApiClient.NvidiaResponse>() {
            @Override
            public void onResponse(Call<NvidiaApiClient.NvidiaResponse> call, Response<NvidiaApiClient.NvidiaResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnAskAi.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && !response.body().choices.isEmpty()) {
                    String aiRawText = response.body().choices.get(0).message.content;

                    parseAndDisplayAiResponse(userQuestion, aiRawText);
                    etAiPrompt.setText(""); // clear search bar
                } else {
                    Toast.makeText(QNAActivity.this, "AI generation failed or server busy.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NvidiaApiClient.NvidiaResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnAskAi.setEnabled(true);
                Toast.makeText(QNAActivity.this, "Internet Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseAndDisplayAiResponse(String question, String aiRawText) {
        String formal = "Not available";
        String casual = "Not available";

        try {
            if (aiRawText.contains("|")) {
                String[] parts = aiRawText.split("\\|");
                formal = parts[0].replace("Formal:", "").trim();
                casual = parts[1].replace("Casual:", "").trim();
            } else {
                formal = aiRawText;
            }
        } catch (Exception e) {
            formal = aiRawText;
        }

        // Add live AI query to the top of the list!
        qnaList.add(0, new QNAModel(question, formal, casual, "Live AI Ask"));
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
    }
}