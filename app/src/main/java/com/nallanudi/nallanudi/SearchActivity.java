package com.nallanudi.nallanudi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    EditText etSearch;
    RecyclerView recyclerView;
    WordAdapter adapter;
    AppDatabase db;
    TextView tvResultCount, tvNoResults, btnClearRecent;
    LinearLayout layoutRecentChips;
    SharedPreferences prefs;
    List<Word> allWords;

    private static final String PREFS_NAME = "NallaNudiPrefs";
    private static final String KEY_RECENT = "recentSearches";
    private static final int MAX_RECENT = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db    = AppDatabase.getInstance(this);
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        allWords = db.wordDao().getAllWords();

        etSearch         = findViewById(R.id.etSearchPage);
        recyclerView     = findViewById(R.id.recyclerSearch);
        tvResultCount    = findViewById(R.id.tvResultCount);
        tvNoResults      = findViewById(R.id.tvNoResults);
        btnClearRecent   = findViewById(R.id.btnClearRecent);
        layoutRecentChips= findViewById(R.id.layoutRecentChips);

        // Back button
        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordAdapter(this, allWords);
        recyclerView.setAdapter(adapter);
        tvResultCount.setText(allWords.size() + " words available");

        // Load recent chips
        loadRecentChips();

        // Clear recent searches
        btnClearRecent.setOnClickListener(v -> {
            prefs.edit().remove(KEY_RECENT).apply();
            loadRecentChips();
        });

        // ✅ Check if opened with a subject filter
        String filter = getIntent().getStringExtra("filter");
        if (filter != null) {
            List<Word> filtered = db.wordDao()
                    .getWordsBySubject(filter);
            adapter.updateList(filtered);
            tvResultCount.setText(
                    filtered.size() + " words in " + filter);
        }

        // ✅ Check if opened with a search query (from recent chips)
        String query = getIntent().getStringExtra("query");
        if (query != null) {
            etSearch.setText(query);
            etSearch.setSelection(query.length());
            performSearch(query);
        }

        // Live search listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable e) {}

            @Override
            public void onTextChanged(
                    CharSequence s, int i, int i1, int i2) {
                String q = s.toString().trim();
                if (q.isEmpty()) {
                    adapter.updateList(allWords);
                    tvResultCount.setText(
                            allWords.size() + " words available");
                    tvNoResults.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    loadRecentChips();
                } else {
                    performSearch(q);
                }
            }
        });
    }

    private void performSearch(String query) {
        List<Word> results = db.wordDao()
                .searchWords("%" + query + "%");
        adapter.updateList(results);

        if (results.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvResultCount.setText(
                    "No results for \"" + query + "\"");
        } else {
            tvNoResults.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            tvResultCount.setText(
                    results.size() + " result(s) for \""
                            + query + "\"");
            saveRecentSearch(query);
            loadRecentChips();
        }
    }

    private void saveRecentSearch(String query) {
        try {
            JSONArray arr = getRecentArray();
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getString(i).equalsIgnoreCase(query)) {
                    arr.remove(i);
                    break;
                }
            }
            JSONArray newArr = new JSONArray();
            newArr.put(query);
            for (int i = 0;
                 i < Math.min(arr.length(), MAX_RECENT - 1);
                 i++) {
                newArr.put(arr.get(i));
            }
            prefs.edit()
                    .putString(KEY_RECENT, newArr.toString())
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray getRecentArray() {
        try {
            String json = prefs.getString(KEY_RECENT, "[]");
            return new JSONArray(json);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private void loadRecentChips() {
        layoutRecentChips.removeAllViews();
        try {
            JSONArray arr = getRecentArray();
            for (int i = 0; i < arr.length(); i++) {
                String term = arr.getString(i);
                TextView chip = new TextView(this);
                chip.setText("🔍 " + term);
                chip.setTextSize(13f);
                chip.setTextColor(
                        Color.parseColor("#4A148C"));
                chip.setBackgroundResource(
                        R.drawable.search_bg);
                chip.setPadding(24, 12, 24, 12);
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMarginEnd(10);
                chip.setLayoutParams(params);
                chip.setOnClickListener(v -> {
                    etSearch.setText(term);
                    etSearch.setSelection(term.length());
                    performSearch(term);
                });
                layoutRecentChips.addView(chip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}