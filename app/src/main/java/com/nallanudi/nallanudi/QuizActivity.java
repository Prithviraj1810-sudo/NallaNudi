package com.nallanudi.nallanudi;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    TextView tvQuestion, tvScore, tvProgress, tvStreak;
    Button btnOpt1, btnOpt2, btnOpt3, btnOpt4;
    ProgressBar progressBar;

    List<Word> wordList;
    int currentIndex = 0;
    int score = 0;
    int streak = 0;
    int total = 10;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        db = AppDatabase.getInstance(this);
        wordList = db.wordDao().getAllWords();
        Collections.shuffle(wordList);
        if (wordList.size() > total) {
            wordList = wordList.subList(0, total);
        }

        tvQuestion  = findViewById(R.id.tvQuestion);
        tvScore     = findViewById(R.id.tvScore);
        tvProgress  = findViewById(R.id.tvProgress);
        tvStreak    = findViewById(R.id.tvStreak);
        progressBar = findViewById(R.id.progressBar);
        btnOpt1     = findViewById(R.id.btnOpt1);
        btnOpt2     = findViewById(R.id.btnOpt2);
        btnOpt3     = findViewById(R.id.btnOpt3);
        btnOpt4     = findViewById(R.id.btnOpt4);

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadQuestion();
    }

    private void loadQuestion() {
        if (currentIndex >= wordList.size()) {
            showResult();
            return;
        }

        Word correct = wordList.get(currentIndex);
        progressBar.setProgress(
                (currentIndex * 100) / total);
        tvProgress.setText(
                "Question " + (currentIndex + 1) + " of " + total);
        tvScore.setText("Score: " + score);
        tvStreak.setText(streak > 1 ? "🔥 " + streak : "");

        tvQuestion.setText(
                "What is the Kannada meaning of:\n\n"
                        + "\"" + correct.englishWord + "\"?");

        // Get 3 wrong options
        List<Word> allWords = db.wordDao().getAllWords();
        allWords.remove(correct);
        Collections.shuffle(allWords);
        List<Word> options = new ArrayList<>();
        options.add(correct);
        options.add(allWords.get(0));
        options.add(allWords.get(1));
        options.add(allWords.get(2));
        Collections.shuffle(options);

        Button[] buttons = {btnOpt1, btnOpt2, btnOpt3, btnOpt4};
        for (int i = 0; i < 4; i++) {
            buttons[i].setText(options.get(i).kannadaWord);
            resetButton(buttons[i]);
            final Word chosen = options.get(i);
            buttons[i].setOnClickListener(v ->
                    checkAnswer((Button) v, chosen, correct, buttons));
        }
    }

    private void checkAnswer(Button clicked, Word chosen,
                             Word correct, Button[] buttons) {
        for (Button b : buttons) b.setClickable(false);

        if (chosen.id == correct.id) {
            clicked.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#388E3C")));
            score++;
            streak++;
            tvStreak.setText(streak > 1 ? "🔥 " + streak : "");
            Toast.makeText(this,
                    "✅ Correct!", Toast.LENGTH_SHORT).show();
        } else {
            clicked.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#C62828")));
            for (Button b : buttons) {
                if (b.getText().toString()
                        .equals(correct.kannadaWord)) {
                    b.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    Color.parseColor("#388E3C")));
                }
            }
            streak = 0;
            Toast.makeText(this,
                    "❌ " + correct.kannadaWord,
                    Toast.LENGTH_SHORT).show();
        }

        currentIndex++;
        new Handler().postDelayed(this::loadQuestion, 1500);
    }

    private void resetButton(Button b) {
        b.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#7B1FA2")));
        b.setTextColor(Color.WHITE);
        b.setClickable(true);
    }

    private void showResult() {
        progressBar.setProgress(100);

        // ✅ Save quiz score to SharedPreferences
        SharedPreferences prefs = getSharedPreferences(
                "NallaNudiPrefs", MODE_PRIVATE);
        int bestScore    = prefs.getInt("bestScore", 0);
        int totalQuizzes = prefs.getInt("totalQuizzes", 0);
        if (score > bestScore) {
            prefs.edit().putInt("bestScore", score).apply();
        }
        prefs.edit()
                .putInt("totalQuizzes", totalQuizzes + 1)
                .apply();

        // Show result message
        tvQuestion.setText(
                "🎉 Quiz Complete!\n\n"
                        + "You scored " + score
                        + " out of " + total + "\n\n"
                        + (score >= 8 ? "Excellent! 🌟" :
                        score >= 5 ? "Good job! 👍" :
                                "Keep practicing! 💪"));

        tvProgress.setText("Finished!");
        tvStreak.setText("");

        btnOpt1.setVisibility(android.view.View.GONE);
        btnOpt2.setVisibility(android.view.View.GONE);
        btnOpt3.setVisibility(android.view.View.GONE);

        btnOpt4.setText("🔁 Play Again");
        btnOpt4.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#4A148C")));
        btnOpt4.setOnClickListener(v -> {
            score = 0;
            streak = 0;
            currentIndex = 0;
            Collections.shuffle(wordList);
            btnOpt1.setVisibility(android.view.View.VISIBLE);
            btnOpt2.setVisibility(android.view.View.VISIBLE);
            btnOpt3.setVisibility(android.view.View.VISIBLE);
            loadQuestion();
        });
    }
}