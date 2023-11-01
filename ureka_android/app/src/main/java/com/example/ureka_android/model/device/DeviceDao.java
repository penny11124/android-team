package com.example.ureka_android.model.device;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ureka_android.model.ticket.TicketDB;

import java.util.List;
import java.util.Map;

@Dao
public interface DeviceDao {
    @Query("SELECT * FROM DeviceDB")
    LiveData<List<DeviceDB>> getAll();

    @Query("SELECT * FROM DeviceDB WHERE id = 1")
    LiveData<DeviceDB> getFirst();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DeviceDB deviceDB);

    @Query("SELECT * FROM DeviceDB JOIN TicketDB ON DeviceDB.id = TicketDB.deviceId")
    LiveData<Map<DeviceDB, List<TicketDB>>> getTicketDBList();

    @Query("SELECT * FROM DeviceDB JOIN TicketDB ON DeviceDB.id = TicketDB.deviceId WHERE DeviceDB.id = :deviceId")
    LiveData<Map<DeviceDB, List<TicketDB>>> getTicketDBList(int deviceId);

    @Query("SELECT * FROM DeviceDB JOIN TicketDB ON DeviceDB.id = TicketDB.deviceId AND TicketDB.publicKey = :publicKey")
    LiveData<Map<DeviceDB, List<TicketDB>>> getTicketDB(byte[] publicKey);
}
