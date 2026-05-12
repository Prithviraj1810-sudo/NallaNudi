package com.nallanudi.nallanudi;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        db = AppDatabase.getInstance(this);

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadStats();
    }

    private void loadStats() {
        int total    = db.wordDao().getWordCount();
        int learned  = db.wordDao().getLearnedCount();
        int saved    = db.wordDao().getSavedCount();
        int sciLearned = db.wordDao()
                .getLearnedCountBySubject("Science");
        int mathLearned = db.wordDao()
                .getLearnedCountBySubject("Math");
        int comLearned = db.wordDao()
                .getLearnedCountBySubject("Commerce");

        // Quiz score from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(
                "NallaNudiPrefs", MODE_PRIVATE);
        int bestScore   = prefs.getInt("bestScore", 0);
        int totalQuizzes = prefs.getInt("totalQuizzes", 0);

        // Overall progress
        TextView tvTotal   = findViewById(R.id.tvTotal);
        TextView tvLearned = findViewById(R.id.tvLearned);
        TextView tvSaved   = findViewById(R.id.tvSaved);
        TextView tvBest    = findViewById(R.id.tvBest);
        TextView tvQuizzes = findViewById(R.id.tvQuizzes);
        TextView tvMotivation = findViewById(R.id.tvMotivation);

        ProgressBar pbOverall  = findViewById(R.id.pbOverall);
        ProgressBar pbScience  = findViewById(R.id.pbScience);
        ProgressBar pbMath     = findViewById(R.id.pbMath);
        ProgressBar pbCommerce = findViewById(R.id.pbCommerce);

        TextView tvSciPct  = findViewById(R.id.tvSciPct);
        TextView tvMathPct = findViewById(R.id.tvMathPct);
        TextView tvComPct  = findViewById(R.id.tvComPct);

        tvTotal.setText(String.valueOf(total));
        tvLearned.setText(String.valueOf(learned));
        tvSaved.setText(String.valueOf(saved));
        tvBest.setText(bestScore + "/10");
        tvQuizzes.setText(String.valueOf(totalQuizzes));

        // Overall progress bar
        int overallPct = total > 0 ? (learned * 100) / total : 0;
        pbOverall.setProgress(overallPct);

        // Subject bars (approx 65-70 words each)
        int sciTotal = 50, mathTotal = 50, comTotal = 50;
        int sciPct  = (sciLearned  * 100) / sciTotal;
        int mathPct = (mathLearned * 100) / mathTotal;
        int comPct  = (comLearned  * 100) / comTotal;

        pbScience.setProgress(sciPct);
        pbMath.setProgress(mathPct);
        pbCommerce.setProgress(comPct);

        tvSciPct.setText(sciLearned + "/" + sciTotal);
        tvMathPct.setText(mathLearned + "/" + mathTotal);
        tvComPct.setText(comLearned + "/" + comTotal);

        // Motivation message
        String msg;
        if (learned == 0)     msg = "Start learning to track progress! 💪";
        else if (overallPct < 25) msg = "Great start! Keep going! 🌱";
        else if (overallPct < 50) msg = "You're doing well! 🌟";
        else if (overallPct < 75) msg = "Halfway there! Amazing! 🔥";
        else if (overallPct < 100) msg = "Almost done! Incredible! 🚀";
        else msg = "You learned all words! 🏆 Champion!";
        tvMotivation.setText(msg);
    }
}
