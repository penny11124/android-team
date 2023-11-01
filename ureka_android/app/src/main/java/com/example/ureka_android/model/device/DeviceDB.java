package com.example.ureka_android.model.device;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DeviceDB {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    int session = 0;
    String contract_address;

    public DeviceDB() {
    }
}