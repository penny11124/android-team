package com.example.ureka_android.model.user;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserDB {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int sessionNum;

    public UserDB() {
    }
}