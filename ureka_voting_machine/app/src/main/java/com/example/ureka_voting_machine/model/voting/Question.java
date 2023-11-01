package com.example.ureka_voting_machine.model.voting;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class Question {
    @PrimaryKey(autoGenerate = true)
    public int questionId;
    public int votingIdMap;
    public String topic;
    public int selected;

    //    @Ignore
    private ArrayList<Option> options;

    public Question() {
    }

    public Question(String topic, ArrayList<Option> options) {
        this.topic = topic;
        this.options = options;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }
}
