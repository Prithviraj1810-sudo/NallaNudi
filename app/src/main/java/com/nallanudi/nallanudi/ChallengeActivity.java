package com.nallanudi.nallanudi;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChallengeActivity extends AppCompatActivity {

    TextView tvQuestion, tvScore, tvHighScore,
            tvTimer, tvStreak, btnBack, tvLives;
    Button btnOpt1, btnOpt2, btnOpt3, btnOpt4;
    ProgressBar timerBar;

    List<Word> wordList;
    int currentIndex = 0;
    int score        = 0;
    int highScore    = 0;
    int streak       = 0;
    int lives        = 3;
    CountDownTimer countDownTimer;
    AppDatabase db;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        db        = AppDatabase.getInstance(this);
        prefs     = getSharedPreferences(
                "NallaNudiPrefs", MODE_PRIVATE);
        highScore = prefs.getInt("challengeHigh", 0);

        wordList  = db.wordDao().getAllWords();
        Collections.shuffle(wordList);

        tvQuestion  = findViewById(R.id.tvQuestion);
        tvScore     = findViewById(R.id.tvScore);
        tvHighScore = findViewById(R.id.tvHighScore);
        tvTimer     = findViewById(R.id.tvTimer);
        tvStreak    = findViewById(R.id.tvStreak);
        tvLives     = findViewById(R.id.tvLives);
        timerBar    = findViewById(R.id.timerBar);
        btnOpt1     = findViewById(R.id.btnOpt1);
        btnOpt2     = findViewById(R.id.btnOpt2);
        btnOpt3     = findViewById(R.id.btnOpt3);
        btnOpt4     = findViewById(R.id.btnOpt4);
        btnBack     = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            if (countDownTimer != null)
                countDownTimer.cancel();
            finish();
        });

        tvHighScore.setText("Best: " + highScore);
        loadQuestion();
    }

    private void loadQuestion() {
        if (lives <= 0) {
            showGameOver();
            return;
        }

        if (currentIndex >= wordList.size()) {
            Collections.shuffle(wordList);
            currentIndex = 0;
        }

        Word correct = wordList.get(currentIndex);

        tvScore.setText("Score: " + score);
        tvLives.setText(getLivesText());
        tvStreak.setText(streak > 1
                ? "x" + streak : "");

        tvQuestion.setText(
                "Kannada meaning of:\n\n"
                        + correct.englishWord + " ?");

        List<Word> all = db.wordDao().getAllWords();
        all.remove(correct);
        Collections.shuffle(all);

        List<Word> options = new ArrayList<>();
        options.add(correct);
        options.add(all.get(0));
        options.add(all.get(1));
        options.add(all.get(2));
        Collections.shuffle(options);

        Button[] buttons = {
                btnOpt1, btnOpt2, btnOpt3, btnOpt4};

        for (int i = 0; i < 4; i++) {
            buttons[i].setText(
                    options.get(i).kannadaWord);
            resetButton(buttons[i]);
            final Word chosen = options.get(i);
            buttons[i].setOnClickListener(v ->
                    checkAnswer((Button) v,
                            chosen, correct, buttons));
        }

        startTimer(buttons, correct);
    }

    private void startTimer(
            Button[] buttons, Word correct) {
        if (countDownTimer != null)
            countDownTimer.cancel();

        timerBar.setMax(10000);
        timerBar.setProgress(10000);

        countDownTimer = new CountDownTimer(
                10000, 100) {

            @Override
            public void onTick(long ms) {
                timerBar.setProgress((int) ms);
                tvTimer.setText(
                        (ms / 1000 + 1) + "s");

                if (ms < 3000) {
                    tvTimer.setTextColor(
                            Color.parseColor("#C62828"));
                } else {
                    tvTimer.setTextColor(
                            Color.parseColor("#4A148C"));
                }
            }

            @Override
            public void onFinish() {
                tvTimer.setText("0s");
                for (Button b : buttons)
                    b.setClickable(false);

                for (Button b : buttons) {
                    if (b.getText().toString()
                            .equals(correct.kannadaWord)) {
                        b.setBackgroundTintList(
                                android.content.res
                                        .ColorStateList.valueOf(
                                                Color.parseColor(
                                                        "#388E3C")));
                    }
                }

                lives--;
                streak = 0;
                tvLives.setText(getLivesText());
                Toast.makeText(
                        ChallengeActivity.this,
                        "Time up! -1 Life",
                        Toast.LENGTH_SHORT).show();
                currentIndex++;
                new Handler().postDelayed(
                        () -> loadQuestion(), 1500);
            }
        }.start();
    }

    private void checkAnswer(Button clicked,
                             Word chosen, Word correct,
                             Button[] buttons) {

        if (countDownTimer != null)
            countDownTimer.cancel();

        for (Button b : buttons)
            b.setClickable(false);

        if (chosen.id == correct.id) {
            clicked.setBackgroundTintList(
                    android.content.res
                            .ColorStateList.valueOf(
                                    Color.parseColor("#388E3C")));
            score++;
            streak++;

            if (streak >= 5) {
                score++;
                Toast.makeText(this,
                        "STREAK BONUS! +2",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Correct! +1",
                        Toast.LENGTH_SHORT).show();
            }

            if (score > highScore) {
                highScore = score;
                prefs.edit()
                        .putInt("challengeHigh",
                                highScore)
                        .apply();
                tvHighScore.setText(
                        "Best: " + highScore);
            }

        } else {
            clicked.setBackgroundTintList(
                    android.content.res
                            .ColorStateList.valueOf(
                                    Color.parseColor("#C62828")));

            for (Button b : buttons) {
                if (b.getText().toString()
                        .equals(correct.kannadaWord)) {
                    b.setBackgroundTintList(
                            android.content.res
                                    .ColorStateList.valueOf(
                                            Color.parseColor(
                                                    "#388E3C")));
                }
            }
            lives--;
            streak = 0;
            tvLives.setText(getLivesText());
            Toast.makeText(this,
                    "Wrong! -1 Life",
                    Toast.LENGTH_SHORT).show();
        }

        currentIndex++;
        new Handler().postDelayed(
                () -> loadQuestion(), 1500);
    }

    private void resetButton(Button b) {
        b.setBackgroundTintList(
                android.content.res
                        .ColorStateList.valueOf(
                                Color.parseColor("#4A148C")));
        b.setTextColor(Color.WHITE);
        b.setClickable(true);
    }

    private String getLivesText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lives; i++)
            sb.append("H ");
        for (int i = lives; i < 3; i++)
            sb.append("X ");
        return sb.toString().trim();
    }

    private void showGameOver() {
        if (countDownTimer != null)
            countDownTimer.cancel();

        tvQuestion.setText(
                "Game Over!\n\n"
                        + "Final Score: " + score + "\n"
                        + "Best Score: " + highScore + "\n\n"
                        + (score >= highScore
                        ? "New High Score!" :
                        score >= 10 ? "Amazing!" :
                                score >= 5  ? "Good try!" :
                                        "Keep practicing!"));

        tvLives.setText("Game Over");
        tvTimer.setText("");

        btnOpt1.setVisibility(
                android.view.View.GONE);
        btnOpt2.setVisibility(
                android.view.View.GONE);
        btnOpt3.setVisibility(
                android.view.View.GONE);

        btnOpt4.setVisibility(
                android.view.View.VISIBLE);
        btnOpt4.setText("Play Again");
        btnOpt4.setBackgroundTintList(
                android.content.res
                        .ColorStateList.valueOf(
                                Color.parseColor("#4A148C")));

        btnOpt4.setOnClickListener(v -> {
            score  = 0;
            streak = 0;
            lives  = 3;
            currentIndex = 0;
            Collections.shuffle(wordList);
            btnOpt1.setVisibility(
                    android.view.View.VISIBLE);
            btnOpt2.setVisibility(
                    android.view.View.VISIBLE);
            btnOpt3.setVisibility(
                    android.view.View.VISIBLE);
            loadQuestion();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null)
            countDownTimer.cancel();
    }
}