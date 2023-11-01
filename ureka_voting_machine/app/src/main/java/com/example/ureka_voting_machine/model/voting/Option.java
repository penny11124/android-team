package com.example.ureka_voting_machine.model.voting;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Option {
    @PrimaryKey(autoGenerate = true)
    public int optionId;
    public int questionIdMap;
    public int optionNum = 0;
    public int optionPosition;
    public String text;

    public Option() {
    }

    public Option(String text) {
        this.text = text;
    }
}
