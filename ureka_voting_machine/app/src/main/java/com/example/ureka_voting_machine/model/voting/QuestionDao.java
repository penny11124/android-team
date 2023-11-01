package com.example.ureka_voting_machine.model.voting;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Map;

@Dao
public interface QuestionDao {
    @Query("SELECT * FROM Question")
    LiveData<List<Question>> getAll();

    @Query("SELECT * FROM Question")
    List<Question> getAllNow();

    @Query("SELECT * FROM Question WHERE questionId = 1")
    LiveData<Question> getFirst();

    @Query("SELECT * FROM Question WHERE questionId = 1")
    Question getFirstNow();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Question question);

    @Query("SELECT * FROM question JOIN option ON questionId = questionIdMap")
    Map<Question, List<Option>> getQuestions();

    @Query("DELETE FROM Question")
    void deleteAll();
}
