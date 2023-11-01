package com.example.ureka_android.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.ureka_android.model.device.DeviceDB;
import com.example.ureka_android.model.device.DeviceDao;
import com.example.ureka_android.model.ticket.TicketDB;
import com.example.ureka_android.model.ticket.TicketDao;
import com.example.ureka_android.model.user.UserDB;
import com.example.ureka_android.model.user.UserDao;

@Database(entities = {DeviceDB.class, TicketDB.class, UserDB.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeviceDao deviceDao();
    public abstract UserDao userDao();
    public abstract TicketDao ticketDao();
}