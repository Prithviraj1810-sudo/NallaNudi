package com.nallanudi.nallanudi;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Collections;
import java.util.List;

public class SpellingActivity extends AppCompatActivity {

    TextView tvQuestion, tvProgress,
            tvScore, tvStreak, tvHint, btnBack;
    EditText etAnswer;
    Button btnSubmit, btnSkip, btnHint;
    ProgressBar progressBar;

    List<Word> wordList;
    int currentIndex = 0;
    int score = 0;
    int streak = 0;
    int hintsUsed = 0;
    int total = 10;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spelling);

        db = AppDatabase.getInstance(this);
        wordList = db.wordDao().getAllWords();
        Collections.shuffle(wordList);
        if (wordList.size() > total)
            wordList = wordList.subList(0, total);

        tvQuestion  = findViewById(R.id.tvQuestion);
        tvProgress  = findViewById(R.id.tvProgress);
        tvScore     = findViewById(R.id.tvScore);
        tvStreak    = findViewById(R.id.tvStreak);
        tvHint      = findViewById(R.id.tvHint);
        etAnswer    = findViewById(R.id.etAnswer);
        btnSubmit   = findViewById(R.id.btnSubmit);
        btnSkip     = findViewById(R.id.btnSkip);
        btnHint     = findViewById(R.id.btnHint);
        progressBar = findViewById(R.id.progressBar);
        btnBack     = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        loadQuestion();

        // Submit answer
        btnSubmit.setOnClickListener(v -> {
            String answer = etAnswer.getText()
                    .toString().trim();
            if (answer.isEmpty()) {
                Toast.makeText(this,
                        "Please type your answer!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            checkAnswer(answer);
        });

        // Skip question
        btnSkip.setOnClickListener(v -> {
            Word word = wordList.get(currentIndex);
            Toast.makeText(this,
                    "Answer: " + word.englishWord,
                    Toast.LENGTH_LONG).show();
            streak = 0;
            tvStreak.setText("");
            currentIndex++;
            new Handler().postDelayed(
                    this::loadQuestion, 1500);
        });

        // Show hint
        btnHint.setOnClickListener(v -> {
            Word word = wordList.get(currentIndex);
            hintsUsed++;
            // Show first 3 letters as hint
            String hint = word.englishWord
                    .substring(0, Math.min(3,
                            word.englishWord.length()))
                    + "...";
            tvHint.setText("💡 Hint: " + hint
                    + " (" + word.subject + ")");
            tvHint.setVisibility(
                    android.view.View.VISIBLE);
        });
    }

    private void loadQuestion() {
        if (currentIndex >= wordList.size()) {
            showResult();
            return;
        }

        Word word = wordList.get(currentIndex);
        etAnswer.setText("");
        tvHint.setVisibility(android.view.View.GONE);
        hintsUsed = 0;

        progressBar.setProgress(
                (currentIndex * 100) / total);
        tvProgress.setText(
                "Word " + (currentIndex + 1)
                        + " of " + total);
        tvScore.setText("Score: " + score);
        tvStreak.setText(
                streak > 1 ? "🔥 " + streak : "");

        // Show Kannada meaning, guess English word
        tvQuestion.setText(
                "Type the English word for:\n\n"
                        + word.kannadaWord + "\n\n"
                        + "(" + word.kannadaExplanation + ")");

        btnSubmit.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#4A148C")));
        btnSubmit.setText("✅ Submit");
        btnSubmit.setClickable(true);
    }

    private void checkAnswer(String answer) {
        Word word = wordList.get(currentIndex);
        boolean correct = answer.trim()
                .equalsIgnoreCase(word.englishWord.trim());

        if (correct) {
            btnSubmit.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#2E7D32")));
            btnSubmit.setText("✅ Correct!");
            score++;
            streak++;
            tvStreak.setText(
                    streak > 1 ? "🔥 " + streak : "");
            Toast.makeText(this,
                    "🎉 Correct! " + word.englishWord,
                    Toast.LENGTH_SHORT).show();
        } else {
            btnSubmit.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#C62828")));
            btnSubmit.setText("❌ Wrong!");
            streak = 0;
            tvStreak.setText("");
            Toast.makeText(this,
                    "❌ Answer: " + word.englishWord,
                    Toast.LENGTH_LONG).show();
        }

        btnSubmit.setClickable(false);
        currentIndex++;
        new Handler().postDelayed(
                this::loadQuestion, 2000);
    }

    private void showResult() {
        progressBar.setProgress(100);

        // Save best score
        SharedPreferences prefs =
                getSharedPreferences(
                        "NallaNudiPrefs", MODE_PRIVATE);
        int best = prefs.getInt("spellingBest", 0);
        if (score > best) {
            prefs.edit()
                    .putInt("spellingBest", score)
                    .apply();
        }

        tvQuestion.setText(
                "🎉 Spelling Bee Complete!\n\n"
                        + "You scored " + score
                        + " out of " + total + "\n\n"
                        + (score >= 8 ? "Spelling Champion! 🏆" :
                        score >= 5 ? "Good effort! 👍" :
                                "Keep practicing! 💪"));

        tvProgress.setText("Finished!");
        etAnswer.setVisibility(
                android.view.View.GONE);
        btnHint.setVisibility(
                android.view.View.GONE);
        btnSkip.setVisibility(
                android.view.View.GONE);

        btnSubmit.setText("🔁 Play Again");
        btnSubmit.setClickable(true);
        btnSubmit.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#4A148C")));
        btnSubmit.setOnClickListener(v -> {
            score = 0;
            streak = 0;
            currentIndex = 0;
            Collections.shuffle(wordList);
            etAnswer.setVisibility(
                    android.view.View.VISIBLE);
            btnHint.setVisibility(
                    android.view.View.VISIBLE);
            btnSkip.setVisibility(
                    android.view.View.VISIBLE);
            loadQuestion();
        });
    }
}