package com.example.ureka_voting_machine.model.voting;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OptionDao {
    @Query("SELECT * FROM Option")
    LiveData<List<Option>> getAll();

    @Query("SELECT * FROM Option WHERE optionId = 1")
    LiveData<Option> getFirst();

    @Query("SELECT * FROM Option WHERE optionId = 1")
    Option getFirstNow();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Option option);

    @Query("DELETE FROM Option")
    void deleteAll();
}
