package com.danyliuk.tunnelmaneger;

import static com.danyliuk.tunnelmaneger.constants.Constants.LOCAL_HOST;
import static com.danyliuk.tunnelmaneger.constants.Constants.LOCAL_PORT;
import static com.danyliuk.tunnelmaneger.constants.Constants.NAME_TUNNEL;
import static com.danyliuk.tunnelmaneger.constants.Constants.PRIVATE_KEY;
import static com.danyliuk.tunnelmaneger.constants.Constants.REMOTE_PORT;
import static com.danyliuk.tunnelmaneger.constants.Constants.REVERSE;
import static com.danyliuk.tunnelmaneger.constants.Constants.SERVER_HOST;
import static com.danyliuk.tunnelmaneger.constants.Constants.USER;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danyliuk.tunnelmaneger.adapter.TunnelsAdapter;
import com.danyliuk.tunnelmaneger.connect.Connection;
import com.danyliuk.tunnelmaneger.databinding.ActivityMainBinding;
import com.danyliuk.tunnelmaneger.entity.Tunnel;
import com.danyliuk.tunnelmaneger.viewmodel.MainViewModel;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TunnelsAdapter tunnelsAdapter;
    private MainViewModel viewModel;

    static {
        Security.removeProvider("BC");//first remove default os provider
        Security.insertProviderAt(new BouncyCastleProvider(), 1);//add new provider
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        ActivityMainBinding b = DataBindingUtil.setContentView(this, R.layout.activity_main);

        tunnelsAdapter = new TunnelsAdapter();
        b.rvTunnels.setLayoutManager(new LinearLayoutManager(this));
        b.rvTunnels.setAdapter(tunnelsAdapter);

        //Обработчик нажантия кнопки создания нового туннеля
        b.btCreateTunnel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, TunnelEditActivity.class), TunnelEditActivity.REQUEST_CODE_ACTIVITY);
            }
        });
        //обработчик чекбокса вкл/выкл туннелей
        tunnelsAdapter.setOnPowerTunnelClickListener(position -> {
            Tunnel tunnel = Objects.requireNonNull(viewModel.getLiveDataTunnels().getValue()).get(position);
            CheckBox cbState = Objects.requireNonNull(b.rvTunnels.findViewHolderForAdapterPosition(position)).itemView.findViewById(R.id.cbStateItem);
            if(cbState.isChecked()) {
                viewModel.tunelOn(tunnel);
                b.progressBar.setVisibility(View.VISIBLE);
            } else {
                viewModel.tunelOff((tunnel) );
                b.progressBar.setVisibility(View.VISIBLE);
            }
        });
        //обработчик нажатия кнопки редактирования туннеля
        tunnelsAdapter.setOnEditTunnelClickListener(position -> {
            Tunnel tunnel = Objects.requireNonNull(viewModel.getLiveDataTunnels().getValue()).get(position);
            Intent intent =new Intent(MainActivity.this, TunnelEditActivity.class);
            intent.putExtra(NAME_TUNNEL, tunnel.getNameTunnel());
            intent.putExtra(LOCAL_PORT,  Integer.toString(tunnel.getLocalPort()));
            intent.putExtra(REMOTE_PORT, Integer.toString(tunnel.getRemotePort()));
            intent.putExtra(PRIVATE_KEY, tunnel.getPrivateKey());
            intent.putExtra(USER, tunnel.getUser());
            intent.putExtra(LOCAL_HOST, tunnel.getLocalHost());
            intent.putExtra(SERVER_HOST, tunnel.getServerHost());
            intent.putExtra(REVERSE, tunnel.getReverse());
            startActivityForResult(intent, TunnelEditActivity.REQUEST_CODE_ACTIVITY);
        });
        //обсервер списка туннелей
        viewModel.getLiveDataTunnels().observe(this, tunnels -> {
            tunnelsAdapter.setTunnels(tunnels);
        });

        //обсервер включения туннеля
        viewModel.connection.resultOperationForTunnel.observe(this, res -> {
            b.progressBar.setVisibility(View.INVISIBLE);
            tunnelsAdapter.notifyDataSetChanged();
        });

        //Удаление туттелей свайпом
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Tunnel tunnel = Objects.requireNonNull(viewModel.getLiveDataTunnels().getValue()).get(viewHolder.getAdapterPosition());
                viewModel.deleteTunnelFromDB(tunnel);
            }
        });
        itemTouchHelper.attachToRecyclerView(b.rvTunnels);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == TunnelEditActivity.REQUEST_CODE_ACTIVITY) {
                assert data != null;
                String nameTunnel = data.getStringExtra("NAME_TUNNEL");

                int localPort = data.getIntExtra("LOCAL_PORT", -1);
                int remotePort = data.getIntExtra("REMOTE_PORT", -1);
                String privateKey = data.getStringExtra("PRIVATE_KEY");
                String user = data.getStringExtra("USER");
                String localHost = data.getStringExtra("LOCAL_HOST");
                String serverHost = data.getStringExtra("SERVER_HOST");
                Boolean reverse = data.getBooleanExtra("REVERSE", false);

                Tunnel tunnel = new Tunnel( nameTunnel, localPort, remotePort, privateKey, user, localHost, serverHost, reverse);
                viewModel.insertTunnelToDB(tunnel);
            }
        }
    }

}