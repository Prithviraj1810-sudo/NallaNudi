package com.nallanudi.nallanudi;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<Word> wordList;
    private Context context;
    private AppDatabase db;
    private TextToSpeech ttsEnglish;
    private TextToSpeech ttsKannada;
    private boolean kannadaReady = false;

    public WordAdapter(Context context, List<Word> wordList) {
        this.context  = context;
        this.wordList = wordList;
        this.db       = AppDatabase.getInstance(context);

        // English TTS
        ttsEnglish = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS)
                ttsEnglish.setLanguage(Locale.ENGLISH);
        });

        // Kannada TTS
        ttsKannada = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale kannada = new Locale("kn", "IN");
                int result = ttsKannada.setLanguage(kannada);
                if (result != TextToSpeech.LANG_NOT_SUPPORTED
                        && result != TextToSpeech.LANG_MISSING_DATA)
                    kannadaReady = true;
            }
        });
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);

        holder.tvEnglishWord.setText(word.englishWord);
        holder.tvKannadaWord.setText(word.kannadaWord);
        holder.tvKannadaExplanation.setText(
                word.kannadaExplanation);
        holder.tvSubjectTag.setText(word.subject);

        // Subject tag color
        switch (word.subject) {
            case "Science":
                holder.tvSubjectTag.setBackgroundColor(
                        android.graphics.Color.parseColor("#7B1FA2"));
                break;
            case "Math":
                holder.tvSubjectTag.setBackgroundColor(
                        android.graphics.Color.parseColor("#1565C0"));
                break;
            case "Commerce":
                holder.tvSubjectTag.setBackgroundColor(
                        android.graphics.Color.parseColor("#E65100"));
                break;
        }

        // Save button state
        holder.btnSave.setText(
                word.isSaved ? "✅ Saved" : "⭐ Save");
        holder.btnSave.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        word.isSaved
                                ? android.graphics.Color.parseColor("#388E3C")
                                : android.graphics.Color.parseColor("#4A148C")
                ));

        // ✅ Mark as viewed when shown
        if (!word.isViewed) {
            db.wordDao().markAsViewed(
                    word.id, System.currentTimeMillis());
            word.isViewed  = true;
            word.viewedTime = System.currentTimeMillis();
        }

        // 🔊 English pronunciation
        holder.btnPronounce.setOnClickListener(v -> {
            ttsEnglish.stop();
            ttsEnglish.speak(word.englishWord,
                    TextToSpeech.QUEUE_FLUSH, null, null);
        });

        // 🔊 Kannada pronunciation
        holder.btnPronounceKannada.setOnClickListener(v -> {
            if (kannadaReady) {
                ttsKannada.stop();
                ttsKannada.speak(
                        word.kannadaWord + ". "
                                + word.kannadaExplanation,
                        TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(context,
                        "Kannada voice not available.\n"
                                + "Install Kannada TTS in Settings.",
                        Toast.LENGTH_LONG).show();
            }
        });

        // ⭐ Save toggle
        holder.btnSave.setOnClickListener(v -> {
            word.isSaved = !word.isSaved;
            db.wordDao().update(word);
            notifyItemChanged(position);
            Toast.makeText(context,
                    word.isSaved
                            ? word.englishWord + " saved! ⭐"
                            : word.englishWord + " removed",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public void updateList(List<Word> newList) {
        this.wordList = newList;
        notifyDataSetChanged();
    }

    public void shutdown() {
        if (ttsEnglish != null) ttsEnglish.shutdown();
        if (ttsKannada != null) ttsKannada.shutdown();
    }

    public static class WordViewHolder
            extends RecyclerView.ViewHolder {
        TextView tvEnglishWord, tvKannadaWord,
                tvKannadaExplanation, tvSubjectTag;
        Button btnPronounce, btnPronounceKannada, btnSave;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEnglishWord        = itemView.findViewById(R.id.tvEnglishWord);
            tvKannadaWord        = itemView.findViewById(R.id.tvKannadaWord);
            tvKannadaExplanation = itemView.findViewById(R.id.tvKannadaExplanation);
            tvSubjectTag         = itemView.findViewById(R.id.tvSubjectTag);
            btnPronounce         = itemView.findViewById(R.id.btnPronounce);
            btnPronounceKannada  = itemView.findViewById(R.id.btnPronounceKannada);
            btnSave              = itemView.findViewById(R.id.btnSave);
        }
    }
}