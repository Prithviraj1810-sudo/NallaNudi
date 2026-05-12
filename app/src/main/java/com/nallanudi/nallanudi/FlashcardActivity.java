package com.nallanudi.nallanudi;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Collections;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity {

    TextView tvCardFront, tvCardBack, tvCardSubject;
    TextView tvProgress, tvStreak, btnBack;
    Button btnFlip, btnKnow, btnDontKnow, btnShuffle;
    android.widget.FrameLayout cardFront, cardBack;

    List<Word> wordList;
    int currentIndex = 0;
    int knownCount = 0;
    int streak = 0;
    boolean isFlipped = false;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        db = AppDatabase.getInstance(this);
        wordList = db.wordDao().getAllWords();
        Collections.shuffle(wordList);

        // Link views
        tvCardFront   = findViewById(R.id.tvCardFront);
        tvCardBack    = findViewById(R.id.tvCardBack);
        tvCardSubject = findViewById(R.id.tvCardSubject);
        tvProgress    = findViewById(R.id.tvProgress);
        tvStreak      = findViewById(R.id.tvStreak);
        btnBack       = findViewById(R.id.btnBack);
        btnFlip       = findViewById(R.id.btnFlip);
        btnKnow       = findViewById(R.id.btnKnow);
        btnDontKnow   = findViewById(R.id.btnDontKnow);
        btnShuffle    = findViewById(R.id.btnShuffle);
        cardFront     = findViewById(R.id.cardFront);
        cardBack      = findViewById(R.id.cardBack);

        btnBack.setOnClickListener(v -> finish());

        // Show first card
        showCard();

        // Flip card
        btnFlip.setOnClickListener(v -> flipCard());

        // Also tap card to flip
        cardFront.setOnClickListener(v -> flipCard());
        cardBack.setOnClickListener(v -> flipCard());

        // Know it ✅
        btnKnow.setOnClickListener(v -> {
            Word word = wordList.get(currentIndex);
            word.isLearned = true;
            db.wordDao().update(word);
            knownCount++;
            streak++;
            tvStreak.setText(streak > 1 ? "🔥 " + streak : "");
            nextCard();
        });

        // Don't know ❌
        btnDontKnow.setOnClickListener(v -> {
            streak = 0;
            tvStreak.setText("");
            nextCard();
        });

        // Shuffle
        btnShuffle.setOnClickListener(v -> {
            Collections.shuffle(wordList);
            currentIndex = 0;
            knownCount = 0;
            streak = 0;
            isFlipped = false;
            tvStreak.setText("");
            showCard();
        });
    }

    private void showCard() {
        if (currentIndex >= wordList.size()) {
            showComplete();
            return;
        }

        Word word = wordList.get(currentIndex);

        // Reset to front
        isFlipped = false;
        cardFront.setVisibility(android.view.View.VISIBLE);
        cardBack.setVisibility(android.view.View.GONE);
        btnFlip.setText("👆 Tap to see Kannada meaning");

        tvCardFront.setText(word.englishWord);
        tvCardBack.setText(
                word.kannadaWord + "\n\n" + word.kannadaExplanation);
        tvCardSubject.setText(word.subject);

        tvProgress.setText(
                (currentIndex + 1) + " / " + wordList.size()
                        + "  |  ✅ " + knownCount + " known");

        // Subject tag color
        switch (word.subject) {
            case "Science":
                tvCardSubject.setBackgroundColor(
                        Color.parseColor("#7B1FA2")); break;
            case "Math":
                tvCardSubject.setBackgroundColor(
                        Color.parseColor("#1565C0")); break;
            case "Commerce":
                tvCardSubject.setBackgroundColor(
                        Color.parseColor("#E65100")); break;
        }
    }

    private void flipCard() {
        if (!isFlipped) {
            // Show back (Kannada)
            cardFront.setVisibility(android.view.View.GONE);
            cardBack.setVisibility(android.view.View.VISIBLE);
            btnFlip.setText("👆 Tap to see English word");
            isFlipped = true;
        } else {
            // Show front (English)
            cardFront.setVisibility(android.view.View.VISIBLE);
            cardBack.setVisibility(android.view.View.GONE);
            btnFlip.setText("👆 Tap to see Kannada meaning");
            isFlipped = false;
        }
    }

    private void nextCard() {
        currentIndex++;
        isFlipped = false;
        showCard();
    }

    private void showComplete() {
        cardFront.setVisibility(android.view.View.VISIBLE);
        cardBack.setVisibility(android.view.View.GONE);
        tvCardFront.setText(
                "🎉 Session Complete!\n\n"
                        + "You knew " + knownCount
                        + " out of " + wordList.size() + " words!\n\n"
                        + (knownCount >= wordList.size() * 0.8
                        ? "Excellent! 🌟"
                        : knownCount >= wordList.size() * 0.5
                        ? "Good job! 👍"
                        : "Keep practicing! 💪"));
        tvProgress.setText("Finished! ✅");
        btnKnow.setVisibility(android.view.View.GONE);
        btnDontKnow.setVisibility(android.view.View.GONE);
        btnFlip.setText("🔁 Study Again");
        btnFlip.setOnClickListener(v -> {
            currentIndex = 0;
            knownCount = 0;
            streak = 0;
            isFlipped = false;
            Collections.shuffle(wordList);
            btnKnow.setVisibility(android.view.View.VISIBLE);
            btnDontKnow.setVisibility(android.view.View.VISIBLE);
            btnFlip.setText("👆 Tap to see Kannada meaning");
            btnFlip.setOnClickListener(v2 -> flipCard());
            showCard();
        });
    }
}