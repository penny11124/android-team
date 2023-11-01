package com.example.ureka_android.model.user;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Map;

@Dao
public interface UserDao {
    @Query("SELECT * FROM UserDB")
    LiveData<List<UserDB>> getAll();

    @Query("SELECT * FROM UserDB WHERE id = 1")
    LiveData<UserDB> getFirst();

    @Query("SELECT * FROM UserDB WHERE id = 1")
    UserDB getFirstNow();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserDB userDB);

    @Query("SELECT * FROM UserDB JOIN DeviceDB ON UserDB.id = DeviceDB.userId")
    LiveData<Map<UserDB, List<UserDB>>> getDeviceDBList();

    @Update
    void updateUser(UserDB user);

}
