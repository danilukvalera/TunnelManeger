package com.danyliuk.tunnelmaneger.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.danyliuk.tunnelmaneger.connect.Connection;
import com.danyliuk.tunnelmaneger.dao.TunelsDataBase;
import com.danyliuk.tunnelmaneger.entity.Tunnel;
import com.jcraft.jsch.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {
    public MainViewModel(@NonNull Application application) throws ExecutionException, InterruptedException {
        super(application);
        this.context = application.getApplicationContext();
        database = TunelsDataBase.getInstance(getApplication());

//            insertTunnelToDB(new Tunnel("R_Android_vet", 5901, 35901, "", "ubuntu", "localhost", "demo2.obemannadbutik.se", false));
//            insertTunnelToDB(new Tunnel("L_vet_Android", 35901, 35901, "", "ubuntu", "localhost", "demo2.obemannadbutik.se", true));
//            insertTunnelToDB(new Tunnel("L_Android_E5570", 5901, 5901, "", "valerii", "localhost", "192.168.1.106", true));
//            insertTunnelToDB(new Tunnel("R_Android_E5570", 35901, 35901, "", "valerii", "localhost", "192.168.1.106", false));

//            insertTunnelToDB(new Tunnel("R_E5570_vet", 5901, 35901, "", "ubuntu", "192.168.1.106", "demo2.obemannadbutik.se", false));
//            insertTunnelToDB(new Tunnel("L_vet_E5570", 35901, 35901, "", "ubuntu", "192.168.1.106", "demo2.obemannadbutik.se", true));

        setTunnelsFromDb(getAllTunnelsFromDB());

    }

    @Override
    protected void onCleared() {
        super.onCleared();
                for (Tunnel tunnel : Objects.requireNonNull(liveDataTunnels.getValue())) {
            if (tunnel.getSession() != null) {
                tunelOff(tunnel );
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private static TunelsDataBase database;
    public Connection connection = new Connection();
    private final MutableLiveData<List<Tunnel>> liveDataTunnels = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Tunnel>> getLiveDataTunnels() {
        return liveDataTunnels;
    }
    public void setLiveDataTunnels(List<Tunnel> tunnels) {
        this.liveDataTunnels.setValue(tunnels);
    }

    public void tunelOn(Tunnel tunnel) {
       this.connection.createTunnel(tunnel);
    }
    public void tunelOff(Tunnel tunnel) {
        this.connection.removeTunnel(tunnel);
    }

    //вставить Туннель в БД
    public void insertTunnelToDB(Tunnel tunnel) {
        List<Tunnel> tunnels = new ArrayList<>();
        try {
            tunnels = new InsertTask().execute(tunnel).get();
        } catch (ExecutionException e) {

        } catch (InterruptedException e) {

        }
        setTunnelsFromDb(tunnels);
    }
    private static class InsertTask extends AsyncTask<Tunnel, Void, List<Tunnel>> {
        @Override
        protected List<Tunnel> doInBackground(Tunnel... tunnels) {
            if (tunnels != null && tunnels.length > 0) {
               database.tunnelDao().insertTunnel(tunnels[0]);
            }
            return database.tunnelDao().getAllTunnels();
        }
    }

    //скопировать в список полученный из БД состояние тунелей и и установить его в liveData
    private void setTunnelsFromDb (List<Tunnel> tunnelsFromDb) {
        List<Tunnel> tempTunnels = new ArrayList<>();
        for (Tunnel tunnelDb : tunnelsFromDb) {
            String name = tunnelDb.getNameTunnel();
            for(Tunnel tunnel : Objects.requireNonNull(liveDataTunnels.getValue())) {
                if (tunnel.getNameTunnel().equals(name)) {
                    tunnelDb.setSession(tunnel.getSession());
                    tunnelDb.setChannel(tunnel.getChannel());
                    break;
                }
            }
            tempTunnels.add(tunnelDb);
        }
        liveDataTunnels.setValue(tempTunnels);
    }

    //получить все туннели из БД
    public List<Tunnel> getAllTunnelsFromDB() {
        try {
            return new GetAllTask().execute().get();
        } catch (ExecutionException e) {
            return new ArrayList<>();
        } catch (InterruptedException e) {
            return new ArrayList<>();
        }
    }

    private static class GetAllTask extends AsyncTask<Void, Void, List<Tunnel>> {
        @Override
        protected List<Tunnel> doInBackground(Void... voids) {
            return database.tunnelDao().getAllTunnels();
        }
    }

    //получить туннель по имени из БД
    public Tunnel getTunnelByNameFromDB(String tunnelName) {
        try {
            return new GetByNameTask().execute(tunnelName).get();
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }
    private static class GetByNameTask extends AsyncTask<String, Void, Tunnel> {
        @Override
        protected Tunnel doInBackground(String... strings) {
            if (strings != null && strings.length > 0) {
                return database.tunnelDao().getTunnelByName(strings[0]);
            }
            return null;
        }
    }

    //удалить туннель из БД
    public void deleteTunnelFromDB(Tunnel tunnel) {
        String prKey = tunnel.getPrivateKey();
        if (prKey != null) {
            removeFileKey(prKey);
        }
        new DeleteTask().execute(tunnel);
        List<Tunnel> tunnels = getAllTunnelsFromDB();
//        setLiveDataTunnels(tunnels);
        setTunnelsFromDb(tunnels);
    }
    private static class DeleteTask extends AsyncTask<Tunnel, Void, Void> {
        @Override
        protected Void doInBackground(Tunnel... tunnels) {
            if (tunnels != null && tunnels.length > 0) {
                database.tunnelDao().deleteTunnel(tunnels[0]);
            }
            return null;
        }
    }

    private void removeFileKey(String fileName) {
        File file = new File(fileName);
        file.delete();
    }
}
