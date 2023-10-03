package com.danyliuk.tunnelmaneger;

import static com.danyliuk.tunnelmaneger.constants.Constants.LOCAL_HOST;
import static com.danyliuk.tunnelmaneger.constants.Constants.LOCAL_PORT;
import static com.danyliuk.tunnelmaneger.constants.Constants.NAME_TUNNEL;
import static com.danyliuk.tunnelmaneger.constants.Constants.PRIVATE_KEY;
import static com.danyliuk.tunnelmaneger.constants.Constants.REMOTE_PORT;
import static com.danyliuk.tunnelmaneger.constants.Constants.REVERSE;
import static com.danyliuk.tunnelmaneger.constants.Constants.SERVER_HOST;
import static com.danyliuk.tunnelmaneger.constants.Constants.USER;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.danyliuk.tunnelmaneger.databinding.ActivityTunnelEditBinding;
import com.danyliuk.tunnelmaneger.viewmodel.EditViewModel;

import java.io.IOException;
import java.util.Objects;

public class TunnelEditActivity extends AppCompatActivity {
    public static int REQUEST_CODE_ACTIVITY = 100;
    public int REQUEST_CODE_SELECTION_FILE_TO_KEY = 101;
    private ActivityTunnelEditBinding b;
    private EditViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tunnel_edit);
        viewModel = new ViewModelProvider(this).get(EditViewModel.class);
        b = DataBindingUtil.setContentView(this, R.layout.activity_tunnel_edit);
        b.btSave.setOnClickListener(view -> addTunnel());
        b.btCancel.setOnClickListener(view -> {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        });
        b.btSelectKey.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, REQUEST_CODE_SELECTION_FILE_TO_KEY);
        });

//        viewModel.getNameTunnel().observe(this, nameTunnel -> {
//            b.etNameTunnel.setText(nameTunnel);
//        });
//        viewModel.getLocalPort().observe(this, localPort -> {
//            b.etLocalPort.setText(localPort);
//        });
//        viewModel.getRemotePort().observe(this, remotePort -> {
//            b.etRemotePort.setText(remotePort);
//        });
        viewModel.getPrivateKey().observe(this, privateKey -> {
            b.etPrivateKey.setText(privateKey);
        });
//        viewModel.getUser().observe(this, user -> {
//            b.etUser.setText(user);
//        });
//        viewModel.getLocalHost().observe(this, localHost -> {
//            b.etLocalHost.setText(localHost);
//        });
//        viewModel.getServerHost().observe(this, serverHost -> {
//            b.etServerHost.setText(serverHost);
//        });
//        viewModel.getReverse().observe(this, reverse -> {
//            b.cbReverse.setChecked(reverse);
//        });
//
        Intent receivedIntent = getIntent();
        b.etNameTunnel.setText(receivedIntent.getStringExtra(NAME_TUNNEL));
        b.etLocalPort.setText(receivedIntent.getStringExtra(LOCAL_PORT));
        b.etRemotePort.setText(receivedIntent.getStringExtra(REMOTE_PORT));
        viewModel.setPrivateKey(receivedIntent.getStringExtra(PRIVATE_KEY));
        b.etUser.setText(receivedIntent.getStringExtra(USER));
        b.etLocalHost.setText(receivedIntent.getStringExtra(LOCAL_HOST));
        b.etServerHost.setText(receivedIntent.getStringExtra(SERVER_HOST));
        b.cbReverse.setChecked(receivedIntent.getBooleanExtra(REVERSE, false));
    }

    private void addTunnel(){
        String nameTunnel = b.etNameTunnel.getText().toString();

        if (viewModel.uriKeyFile != null) {
            viewModel.copyKeyFile(nameTunnel);
        }

        int localPort = -1;
        try {
            localPort = Integer.parseInt( b.etLocalPort.getText().toString());
        } catch (NumberFormatException e) {
//        } catch (Exception e) {

        }
        int remotePort = -1;
        try {
            remotePort = Integer.parseInt(b.etRemotePort.getText().toString());
        } catch (NumberFormatException e) {
//        } catch (Exception e) {

        }
        String privateKey = b.etPrivateKey.getText().toString();
        String user = b.etUser.getText().toString();
        String localHost = b.etLocalHost.getText().toString();
        String serverHost = b.etServerHost.getText().toString();
        Boolean reverse = b.cbReverse.isChecked();

        Intent resultIntent = new Intent();
        resultIntent.putExtra(NAME_TUNNEL, nameTunnel);
        resultIntent.putExtra(LOCAL_PORT, localPort);
        resultIntent.putExtra(REMOTE_PORT, remotePort);
        resultIntent.putExtra(PRIVATE_KEY, privateKey);
        resultIntent.putExtra(USER, user);
        resultIntent.putExtra(LOCAL_HOST, localHost);
        resultIntent.putExtra(SERVER_HOST, serverHost);
        resultIntent.putExtra(REVERSE, reverse);



        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_SELECTION_FILE_TO_KEY) {
                viewModel.uriKeyFile = intent.getData();
                viewModel.setPrivateKey(viewModel.uriKeyFile.getPath());
//                viewModel.copyKeyFile(uriFile);
            }
        }
    }
}