package com.danyliuk.tunnelmaneger.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.danyliuk.tunnelmaneger.entity.Tunnel;

import java.util.List;

@Dao
public interface TunnelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTunnel(Tunnel tunnel);

    @Query("SELECT * FROM tunnels WHERE name_tunnel = :tunnelName")
    Tunnel getTunnelByName(String tunnelName);

    @Query("SELECT * FROM tunnels order by name_tunnel ")
    List<Tunnel> getAllTunnels();

    @Delete
    void deleteTunnel(Tunnel tunnel);
}
