package com.danyliuk.tunnelmaneger.connect;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.danyliuk.tunnelmaneger.entity.Tunnel;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class Connection {
    public MutableLiveData<Boolean> resultOperationForTunnel = new MutableLiveData<>(false);

    public void createTunnel(Tunnel tunnel) {
            new CreateTask().execute(tunnel);
    }
    private class CreateTask extends AsyncTask<Tunnel, Void, Void> {
    @Override
        protected Void doInBackground(Tunnel... tunnels) {
            try {
                JSch jsch = new JSch();
                JSch.setLogger(new MyLogger());

                jsch.addIdentity(tunnels[0].getPrivateKey());

                Session session=jsch.getSession(tunnels[0].getUser(), tunnels[0].getServerHost(), 22);
                session.setConfig("StrictHostKeyChecking", "no");

                MyUserInfo ui = new MyUserInfo();
                session.setUserInfo(ui);
                session.connect(3000); // making a connection with timeout as defined above.
                Channel channel=session.openChannel("shell");
                channel.setInputStream(System.in);
                channel.setOutputStream(System.out);
                channel.connect(3*1000);

                if (tunnels[0].getReverse()) {
                    session.setPortForwardingL(tunnels[0].getLocalPort(), tunnels[0].getLocalHost(), tunnels[0].getRemotePort());
                } else {
                    session.setPortForwardingR(tunnels[0].getRemotePort(), tunnels[0].getLocalHost(), tunnels[0].getLocalPort());
                }

                tunnels[0].setChannel(channel);
                tunnels[0].setSession(session);
                resultOperationForTunnel.postValue(true);

            } catch (Exception e) {
                tunnels[0].setChannel(null);
                tunnels[0].setSession(null);
                resultOperationForTunnel.postValue(false);
                System.out.println(e);
                return null;
            }
            return null;
        }
    }

    public void removeTunnel(Tunnel tunnel) {
        new RemoveTask().execute(tunnel);
    }
    private class RemoveTask extends AsyncTask<Tunnel, Void, Void> {
        @Override
        protected Void doInBackground(Tunnel... tunnels) {
            if (tunnels != null && tunnels[0] != null) {
                Session session = tunnels[0].getSession();
                if (session.isConnected()) {
                    tunnels[0].getChannel().disconnect();
                    session.disconnect();
                }
                tunnels[0].setSession(null);
                resultOperationForTunnel.postValue(false);
            }
            return null;
        }
    }
    public  static class MyUserInfo implements UserInfo{
        public String getPassword(){ return null; }
        public boolean promptYesNo(String str){ return false; }
        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){ return false; }
        public boolean promptPassword(String message){ return false; }
        public void showMessage(String message){ }
    }
    public static class MyLogger implements com.jcraft.jsch.Logger {
        static java.util.Hashtable name=new java.util.Hashtable();
        static{
            name.put(new Integer(DEBUG), "DEBUG: ");
            name.put(new Integer(INFO), "INFO: ");
            name.put(new Integer(WARN), "WARN: ");
            name.put(new Integer(ERROR), "ERROR: ");
            name.put(new Integer(FATAL), "FATAL: ");
        }
        public boolean isEnabled(int level){
            return true;
        }
        public void log(int level, String message){
            System.err.print(name.get(new Integer(level)));
            System.err.println(message);
        }
    }
}
