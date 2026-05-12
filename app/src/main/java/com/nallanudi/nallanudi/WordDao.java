package com.nallanudi.nallanudi;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface WordDao {

    @Insert
    void insert(Word word);

    @Query("SELECT * FROM words ORDER BY englishWord ASC")
    List<Word> getAllWords();

    @Query("SELECT * FROM words WHERE subject = :subject ORDER BY englishWord ASC")
    List<Word> getWordsBySubject(String subject);

    @Query("SELECT * FROM words WHERE englishWord LIKE :query OR kannadaWord LIKE :query")
    List<Word> searchWords(String query);

    @Query("SELECT * FROM words WHERE isSaved = 1")
    List<Word> getSavedWords();

    @Query("SELECT * FROM words WHERE isLearned = 1")
    List<Word> getLearnedWords();

    @Query("SELECT COUNT(*) FROM words")
    int getWordCount();

    @Query("SELECT COUNT(*) FROM words WHERE isSaved = 1")
    int getSavedCount();

    @Query("SELECT COUNT(*) FROM words WHERE isLearned = 1")
    int getLearnedCount();

    @Query("SELECT COUNT(*) FROM words WHERE subject = :subject AND isLearned = 1")
    int getLearnedCountBySubject(String subject);

    @Query("SELECT * FROM words WHERE isViewed = 1 ORDER BY viewedTime DESC")
    List<Word> getViewedWords();

    @Query("UPDATE words SET isViewed = 1, viewedTime = :time WHERE id = :id")
    void markAsViewed(int id, long time);

    @Update
    void update(Word word);
}