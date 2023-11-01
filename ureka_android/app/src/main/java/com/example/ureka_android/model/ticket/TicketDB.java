package com.example.ureka_android.model.ticket;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.security.PublicKey;

@Entity
public class TicketDB {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int deviceId;

    public String ticketString;

    // device pk
    public byte[] publicKey;

    public TicketDB(String ticketString, byte[] publicKey) {
        this.ticketString = ticketString;
        this.publicKey = publicKey;
    }
}
