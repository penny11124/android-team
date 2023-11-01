package com.example.ureka_voting_machine.model.device;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ureka_voting_machine.model.ticket.TicketDB;

import java.util.List;
import java.util.Map;

@Dao
public interface DeviceDao {
    @Query("SELECT * FROM DeviceDB")
    LiveData<List<DeviceDB>> getAll();

    @Query("SELECT * FROM DeviceDB WHERE id = 1")
    LiveData<DeviceDB> getFirst();

    @Query("SELECT * FROM DeviceDB WHERE id = 1")
    DeviceDB getFirstNow();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DeviceDB deviceDB);

    @Query("SELECT * FROM DeviceDB JOIN TicketDB WHERE DeviceDB.id = TicketDB.deviceId")
    LiveData<Map<DeviceDB, List<TicketDB>>> getTicketDBList();

    @Query("SELECT * FROM DeviceDB JOIN TicketDB WHERE DeviceDB.id = TicketDB.deviceId AND TicketDB.publicKey = :publicKey")
    LiveData<Map<DeviceDB, List<TicketDB>>> getTicketDB(String publicKey);

    @Query("UPDATE DeviceDB SET address= :address WHERE DeviceDB.id = 1")
    void updateAddress(byte[] address);

    @Query("UPDATE DeviceDB SET address= :address WHERE DeviceDB.id = 1")
    void updateAddress(String address);

    @Query("UPDATE DeviceDB SET uticket= :uticket WHERE DeviceDB.id = 1")
    void updateUTicket(byte[] uticket);

    @Query("UPDATE DeviceDB SET rticket= :rticket WHERE DeviceDB.id = 1")
    void updateRTicket(byte[] rticket);

}
