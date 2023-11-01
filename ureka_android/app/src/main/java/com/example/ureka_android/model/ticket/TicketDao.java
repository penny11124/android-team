package com.example.ureka_android.model.ticket;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface TicketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TicketDB ticketDB);

    @Query("SELECT * FROM TicketDB WHERE TicketDB.publicKey = :devicePK")
    LiveData<TicketDB> getTicket(byte[] devicePK);

    @Query("SELECT * FROM TicketDB WHERE TicketDB.publicKey = :devicePK")
    TicketDB getTicketNow(byte[] devicePK);

}
