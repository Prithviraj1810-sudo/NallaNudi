package com.nallanudi.nallanudi;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "words")
public class Word {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String englishWord;
    public String kannadaWord;
    public String kannadaExplanation;
    public String subject;
    public boolean isSaved;
    public boolean isLearned;
    public boolean isViewed;
    public long viewedTime;

    public Word(String englishWord, String kannadaWord,
                String kannadaExplanation, String subject) {
        this.englishWord     = englishWord;
        this.kannadaWord     = kannadaWord;
        this.kannadaExplanation = kannadaExplanation;
        this.subject         = subject;
        this.isSaved         = false;
        this.isLearned       = false;
        this.isViewed        = false;
        this.viewedTime      = 0;
    }
}