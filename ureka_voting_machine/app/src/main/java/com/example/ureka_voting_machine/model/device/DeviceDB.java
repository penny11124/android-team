package com.example.ureka_voting_machine.model.device;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DeviceDB {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int session = 0;
    public String address;
    public byte[] uticket;
    public byte[] rticket;

    public DeviceDB() {
    }
}