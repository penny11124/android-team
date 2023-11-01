package com.example.ureka_voting_machine.model.voting;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Map;


@Dao
public interface VotingDao {
    @Query("SELECT * FROM Voting")
    LiveData<List<Voting>> getAll();

    @Query("SELECT * FROM Voting WHERE votingId = 1")
    LiveData<Voting> getFirst();

    @Query("SELECT * FROM Voting WHERE votingId = 1")
    Voting getFirstNow();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Voting voting);

    @Query("SELECT * FROM Voting JOIN Question ON Voting.votingId = Question.votingIdMap")
    Map<Voting, List<Question>> getQuestions();
}
