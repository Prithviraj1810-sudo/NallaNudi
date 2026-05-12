package com.nallanudi.nallanudi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.List;
import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    TextView tvWordOfDay, tvWordOfDayKannada;
    LinearLayout layoutRecentChips;
    AppDatabase db;
    SharedPreferences prefs;
    boolean isDarkMode = false;

    private static final String PREFS_NAME = "NallaNudiPrefs";
    private static final String KEY_RECENT = "recentSearches";
    private static final String KEY_DARK   = "darkMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db    = AppDatabase.getInstance(this);
        prefs = getSharedPreferences(
                PREFS_NAME, Context.MODE_PRIVATE);
        isDarkMode = prefs.getBoolean(KEY_DARK, false);

        tvWordOfDay        = findViewById(R.id.tvWordOfDay);
        tvWordOfDayKannada = findViewById(R.id.tvWordOfDayKannada);
        layoutRecentChips  = findViewById(R.id.layoutRecentChips);

        // Dark Mode toggle
        TextView btnDarkMode = findViewById(R.id.btnDarkMode);
        btnDarkMode.setText(isDarkMode ? "☀️" : "🌙");
        btnDarkMode.setOnClickListener(v -> {
            isDarkMode = !isDarkMode;
            prefs.edit()
                    .putBoolean(KEY_DARK, isDarkMode)
                    .apply();
            recreate();
        });

        // Search
        TextView btnGoToSearch = findViewById(R.id.btnGoToSearch);
        btnGoToSearch.setOnClickListener(v ->
                startActivity(new Intent(
                        this, SearchActivity.class)));

        // Subject cards
        CardView btnScience  = findViewById(R.id.btnScience);
        CardView btnMath     = findViewById(R.id.btnMath);
        CardView btnCommerce = findViewById(R.id.btnCommerce);
        btnScience.setOnClickListener(v ->
                openSearch("Science"));
        btnMath.setOnClickListener(v ->
                openSearch("Math"));
        btnCommerce.setOnClickListener(v ->
                openSearch("Commerce"));

        // Feature cards
        CardView btnMyList    = findViewById(R.id.btnMyList);
        CardView btnFlashcard = findViewById(R.id.btnFlashcard);
        CardView btnQuiz      = findViewById(R.id.btnQuiz);
        CardView btnProgress  = findViewById(R.id.btnProgress);
        CardView btnSpelling  = findViewById(R.id.btnSpelling);
        CardView btnChallenge = findViewById(R.id.btnChallenge);
        CardView btnHistory   = findViewById(R.id.btnHistory);

        btnMyList.setOnClickListener(v ->
                startActivity(new Intent(
                        this, MyListActivity.class)));
        btnFlashcard.setOnClickListener(v ->
                startActivity(new Intent(
                        this, FlashcardActivity.class)));
        btnQuiz.setOnClickListener(v ->
                startActivity(new Intent(
                        this, QuizActivity.class)));
        btnProgress.setOnClickListener(v ->
                startActivity(new Intent(
                        this, ProgressActivity.class)));
        btnSpelling.setOnClickListener(v ->
                startActivity(new Intent(
                        this, SpellingActivity.class)));
        btnChallenge.setOnClickListener(v ->
                startActivity(new Intent(
                        this, ChallengeActivity.class)));
        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(
                        this, WordHistoryActivity.class)));

        setWordOfTheDay();
        loadRecentChips();
    }

    private void openSearch(String filter) {
        Intent intent = new Intent(
                this, SearchActivity.class);
        intent.putExtra("filter", filter);
        startActivity(intent);
    }

    private void setWordOfTheDay() {
        List<Word> all = db.wordDao().getAllWords();
        if (!all.isEmpty()) {
            int index = java.util.Calendar.getInstance()
                    .get(java.util.Calendar.DAY_OF_YEAR)
                    % all.size();
            Word word = all.get(index);
            tvWordOfDay.setText(word.englishWord);
            tvWordOfDayKannada.setText(
                    word.kannadaWord + " — "
                            + word.kannadaExplanation);
        }
    }

    private void loadRecentChips() {
        layoutRecentChips.removeAllViews();
        try {
            String json = prefs.getString(
                    KEY_RECENT, "[]");
            JSONArray arr = new JSONArray(json);

            if (arr.length() == 0) {
                TextView empty = new TextView(this);
                empty.setText("No recent searches yet");
                empty.setTextColor(
                        Color.parseColor("#9C27B0"));
                empty.setTextSize(12f);
                layoutRecentChips.addView(empty);
                return;
            }

            for (int i = 0; i < arr.length(); i++) {
                String fullTerm = arr.getString(i);
                String display = fullTerm.length() > 10
                        ? fullTerm.substring(0, 10) + "…"
                        : fullTerm;

                TextView chip = new TextView(this);
                chip.setText(display);
                chip.setTextSize(11f);
                chip.setTextColor(
                        Color.parseColor("#4A148C"));
                chip.setBackgroundResource(
                        R.drawable.search_bg_white);
                chip.setPadding(20, 10, 20, 10);

                LinearLayout.LayoutParams p =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                p.setMarginEnd(8);
                chip.setLayoutParams(p);

                chip.setOnClickListener(v -> {
                    Intent intent = new Intent(
                            this, SearchActivity.class);
                    intent.putExtra("query", fullTerm);
                    startActivity(intent);
                });
                layoutRecentChips.addView(chip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWordOfTheDay();
        loadRecentChips();
    }
}