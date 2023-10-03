package com.danyliuk.tunnelmaneger.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.danyliuk.tunnelmaneger.entity.Tunnel;

@Database(entities = {Tunnel.class}, version = 1)
public abstract class TunelsDataBase extends RoomDatabase {
    private static final String DB_NAME = "tunnels.db";
    private static TunelsDataBase database;
    private static final Object LOCK = new Object();

    public static TunelsDataBase getInstance(Context context) {
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context,TunelsDataBase.class, DB_NAME).build();
            }
        }
        return database;
    }
    public abstract TunnelDao tunnelDao();
}
