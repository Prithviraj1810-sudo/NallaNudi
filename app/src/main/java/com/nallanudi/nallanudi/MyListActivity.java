package com.nallanudi.nallanudi;



import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        AppDatabase db = AppDatabase.getInstance(this);
        List<Word> savedWords = db.wordDao().getSavedWords();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewMyList);
        TextView tvEmpty = findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (savedWords.isEmpty()) {
            tvEmpty.setText(
                    "No words saved yet!\nTap ⭐ Save on any word to add it here.");
        } else {
            WordAdapter adapter = new WordAdapter(this, savedWords);
            recyclerView.setAdapter(adapter);
        }
    }
}
