package com.example.ureka_voting_machine.model.voting;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.ureka_voting_machine.ureka.CommandType;

@Entity
public class Voting {
    @PrimaryKey(autoGenerate = true)
    public int votingId = 1;
    public int state = 0; // 0: wait; 1: voting; 2: setting; 3: billing;
    public CommandType commandType = CommandType.DEFAULT;
}
