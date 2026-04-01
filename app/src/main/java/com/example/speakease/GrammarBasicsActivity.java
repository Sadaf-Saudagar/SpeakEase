package com.example.speakease;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrammarBasicsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GrammarAdapter adapter;
    private LinearLayout categoryContainer;
    private Map<String, List<GrammarCard>> database = new HashMap<>();
    private Button lastSelectedButton = null;

    // The Model Class nested inside to prevent namespace errors
    public static class GrammarCard {
        public String title, description, formula, example;
        public GrammarCard(String title, String description, String formula, String example) {
            this.title = title;
            this.description = description;
            this.formula = formula;
            this.example = example;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar);

        recyclerView = findViewById(R.id.rvGrammarCards);
        categoryContainer = findViewById(R.id.categoryContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupDatabase();
        createCategoryButtons();

        // Default Load: Parts of Speech
        loadCategory("Parts of Speech", (Button) categoryContainer.getChildAt(1));

        findViewById(R.id.btnBackGrammar).setOnClickListener(v -> finish());
    }

    private void setupDatabase() {

        // 1. BASIC FORMATION
        List<GrammarCard> basic = new ArrayList<>();
        basic.add(new GrammarCard(
                "Standard Formula",
                "Basic sentence structure",
                "Subject + Verb + Object",
                "I (Pronoun) + love (Verb) + coding (Noun)."
        ));
        basic.add(new GrammarCard(
                "Extended Structure",
                "Adding more detail to a sentence",
                "Subject + Verb + Object + Adverb + Prepositional Phrase",
                "He (Pronoun) + reads (Verb) + books (Noun) + quickly (Adverb) + in the library (Prepositional Phrase)."
        ));
        basic.add(new GrammarCard(
                "With Adjective",
                "Including descriptive elements",
                "Subject + Verb + Adjective + Object",
                "She (Pronoun) + bought (Verb) + a beautiful (Adjective) + dress (Noun)."
        ));
        basic.add(new GrammarCard(
                "Compound Structure",
                "Joining two clauses using a conjunction",
                "Clause + Conjunction + Clause",
                "I (Pronoun) + study (Verb) + hard (Adverb), and I (Pronoun) + succeed (Verb)."
        ));
        basic.add(new GrammarCard(
                "Complex Structure",
                "Using dependent clauses",
                "Main Clause + Subordinate Clause",
                "I (Pronoun) + will come (Verb) + if you (Pronoun) + invite (Verb) + me (Pronoun)."
        ));
        database.put("Basic Formation", basic);


// 2. PARTS OF SPEECH
        List<GrammarCard> pos = new ArrayList<>();
        pos.add(new GrammarCard(
                "Noun",
                "Names of people, places, or things",
                "Noun + Verb + Prepositional Phrase",
                "The cat (Noun) + sat (Verb) + on the mat (Prepositional Phrase)."
        ));
        pos.add(new GrammarCard(
                "Pronoun",
                "Replaces a noun in a sentence",
                "Pronoun + Verb + Pronoun",
                "I (Pronoun) + see (Verb) + them (Pronoun)."
        ));
        pos.add(new GrammarCard(
                "Adverb",
                "Modifies a verb, adjective, or another adverb",
                "Verb + Adverb",
                "She (Pronoun) + speaks (Verb) + softly (Adverb)."
        ));
        pos.add(new GrammarCard(
                "Adjective",
                "Describes a noun or pronoun",
                "Adjective + Noun",
                "A beautiful (Adjective) + garden (Noun)."
        ));
        pos.add(new GrammarCard(
                "Preposition",
                "Shows relationship between words",
                "Preposition + Noun",
                "The book is on (Preposition) + the table (Noun)."
        ));
        database.put("Parts of Speech", pos);


// 3. PRESENT TENSE
        List<GrammarCard> present = new ArrayList<>();
        present.add(new GrammarCard(
                "Simple Present",
                "Describes routines or general facts",
                "Subject + Base Verb",
                "I (Pronoun) + write (Verb)."
        ));
        present.add(new GrammarCard(
                "Present Continuous",
                "Describes an action happening now",
                "Subject + Auxiliary Verb + Verb-ing",
                "They (Pronoun) + are (Auxiliary Verb) + playing (Verb-ing)."
        ));
        present.add(new GrammarCard(
                "Present Perfect",
                "Describes a completed action with present relevance",
                "Subject + Auxiliary Verb + Past Participle",
                "We (Pronoun) + have (Auxiliary Verb) + finished (Past Participle)."
        ));
        present.add(new GrammarCard(
                "Present Perfect Continuous",
                "Describes an action continuing up to the present",
                "Subject + Auxiliary Verb + been + Verb-ing",
                "She (Pronoun) + has (Auxiliary Verb) + been + studying (Verb-ing)."
        ));
        present.add(new GrammarCard(
                "Negative Form",
                "Expresses negation in present tense",
                "Subject + Auxiliary Verb + not + Base Verb",
                "I (Pronoun) + do (Auxiliary Verb) + not + understand (Verb)."
        ));
        database.put("Present Tense", present);


// 4. ARTICLES
        List<GrammarCard> articles = new ArrayList<>();
        articles.add(new GrammarCard(
                "Indefinite Articles",
                "Used for non-specific nouns",
                "Article + Adjective + Noun",
                "A (Article) + big (Adjective) + dog (Noun)."
        ));
        articles.add(new GrammarCard(
                "Definite Article",
                "Used for specific nouns",
                "Article + Adjective + Noun",
                "The (Article) + red (Adjective) + car (Noun)."
        ));
        articles.add(new GrammarCard(
                "Zero Article",
                "Used when no article is required",
                "Noun (no article)",
                "Books (Noun) + are useful (Verb Phrase)."
        ));
        articles.add(new GrammarCard(
                "Article with Proper Noun",
                "Usually no article is used with proper nouns",
                "Proper Noun",
                "Mumbai (Proper Noun) + is beautiful (Verb Phrase)."
        ));
        articles.add(new GrammarCard(
                "Article with Superlatives",
                "Definite article used with superlatives",
                "The + Superlative Adjective + Noun",
                "The (Article) + best (Superlative Adjective) + student (Noun)."
        ));
        database.put("Articles", articles);


// 5. MODAL VERBS
        List<GrammarCard> modals = new ArrayList<>();
        modals.add(new GrammarCard(
                "Ability",
                "Expresses ability",
                "Subject + Modal Verb + Base Verb",
                "I (Pronoun) + can (Modal Verb) + swim (Verb)."
        ));
        modals.add(new GrammarCard(
                "Permission",
                "Asking or giving permission",
                "Modal Verb + Subject + Base Verb",
                "May (Modal Verb) + I (Pronoun) + enter (Verb)?"
        ));
        modals.add(new GrammarCard(
                "Obligation",
                "Expresses duty or necessity",
                "Subject + Modal Verb + Base Verb",
                "You (Pronoun) + must (Modal Verb) + follow (Verb) + the rules."
        ));
        modals.add(new GrammarCard(
                "Possibility",
                "Expresses possibility",
                "Subject + Modal Verb + Base Verb",
                "It (Pronoun) + might (Modal Verb) + rain (Verb)."
        ));
        modals.add(new GrammarCard(
                "Advice",
                "Used to give suggestions",
                "Subject + Modal Verb + Base Verb",
                "You (Pronoun) + should (Modal Verb) + study (Verb)."
        ));
        database.put("Modal Verbs", modals);


// 6. QUESTION FORMATION
        List<GrammarCard> questions = new ArrayList<>();
        questions.add(new GrammarCard(
                "Yes/No Questions",
                "Questions expecting yes or no answers",
                "Auxiliary Verb + Subject + Base Verb",
                "Do (Auxiliary Verb) + you (Pronoun) + understand (Verb)?"
        ));
        questions.add(new GrammarCard(
                "Wh- Questions",
                "Questions using question words",
                "Wh-word + Auxiliary Verb + Subject + Verb",
                "Where + do (Auxiliary Verb) + you (Pronoun) + live (Verb)?"
        ));
        questions.add(new GrammarCard(
                "Tag Questions",
                "Short questions added at the end",
                "Statement + Tag",
                "You (Pronoun) + are (Verb) + coming, aren't you?"
        ));
        questions.add(new GrammarCard(
                "Choice Questions",
                "Offering alternatives",
                "Auxiliary Verb + Subject + Option A or Option B",
                "Do (Auxiliary Verb) + you (Pronoun) + want tea or coffee?"
        ));
        questions.add(new GrammarCard(
                "Indirect Questions",
                "Polite or embedded questions",
                "Introductory Phrase + Clause",
                "Could you tell me where the station is?"
        ));
        database.put("Question Formation", questions);

    }

    private void createCategoryButtons() {
        String[] topics = {"Basic Formation", "Parts of Speech", "Present Tense", "Articles", "Modal Verbs", "Question Formation"};
        for (String topic : topics) {
            Button btn = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 0);
            btn.setLayoutParams(params);
            btn.setText(topic);
            btn.setAllCaps(false);
            btn.setPadding(32, 16, 32, 16);
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

        adapter = new GrammarAdapter(database.get(category));
        recyclerView.setAdapter(adapter);
    }
}