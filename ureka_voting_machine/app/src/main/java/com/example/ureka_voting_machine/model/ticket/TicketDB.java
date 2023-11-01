package com.example.ureka_voting_machine.model.ticket;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TicketDB {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int deviceId;

    public String ticketString;

    public String publicKey;
}
