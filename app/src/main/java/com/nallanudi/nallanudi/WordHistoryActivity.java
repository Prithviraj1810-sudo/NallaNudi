package com.nallanudi.nallanudi;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WordHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_history);

        AppDatabase db = AppDatabase.getInstance(this);
        List<Word> viewedWords = db.wordDao().getViewedWords();

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView recyclerView =
                findViewById(R.id.recyclerHistory);
        TextView tvEmpty =
                findViewById(R.id.tvEmpty);
        TextView tvCount =
                findViewById(R.id.tvCount);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        if (viewedWords.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvCount.setText("0 words viewed");
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            tvCount.setText(
                    viewedWords.size() + " words viewed");
            WordAdapter adapter =
                    new WordAdapter(this, viewedWords);
            recyclerView.setAdapter(adapter);
        }
    }
}