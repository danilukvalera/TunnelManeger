package com.danyliuk.tunnelmaneger.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;


import kotlin.jvm.Transient;

@Entity(tableName = "tunnels", indices = {@Index(value = {"name_tunnel"},unique = true)})
public class Tunnel {
    @Ignore
    public Tunnel(String nameTunnel) {
        this(nameTunnel, -1, -1, "", "", "", "", false);
    }
    public Tunnel(String nameTunnel, int localPort, int remotePort, String privateKey, String user, String localHost, String serverHost, Boolean reverse) {
        this.nameTunnel = nameTunnel;
        this.localPort = localPort;
        this.remotePort = remotePort;
        this.privateKey = privateKey;
        this.user = user;
        this.localHost = localHost;
        this.serverHost = serverHost;
        this.reverse = reverse;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name_tunnel")
    private String nameTunnel;
    @ColumnInfo(name = "local_port")
    private int localPort;
    @ColumnInfo(name = "remote_port")
    private int remotePort;
    @ColumnInfo(name = "private_key")
    private String privateKey;
    @ColumnInfo(name = "user")
    private String user;
    @ColumnInfo(name = "local_host")
    private String localHost;
    @ColumnInfo(name = "server_host")
    private String serverHost;
    @ColumnInfo(name = "reverse")
    private Boolean reverse;

    @Ignore
    private Session session;
    @Ignore
    Channel channel;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNameTunnel() {
        return nameTunnel;
    }
    public void setNameTunnel(String nameTunnel) {
        this.nameTunnel = nameTunnel;
    }
    public int getLocalPort() {
        return localPort;
    }
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }
    public int getRemotePort() {
        return remotePort;
    }
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
    public String getPrivateKey() {
        return privateKey;
    }
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getLocalHost() {
        return localHost;
    }
    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }
    public String getServerHost() {
        return serverHost;
    }
    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }
    public Boolean getReverse() {
        return reverse;
    }
    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }
    public Session getSession() {
        return session;
    }
    public void setSession(Session session) {
        this.session = session;
    }
    public Channel getChannel() {
        return channel;
    }
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
