package com.danyliuk.tunnelmaneger.viewmodel;

import static android.content.Context.MODE_PRIVATE;

import static com.danyliuk.tunnelmaneger.constants.Constants.SUFFIX_NAME_KEY_FILE;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class EditViewModel  extends AndroidViewModel {
    public EditViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }
    private final MutableLiveData<String> privateKey = new MutableLiveData<>("");
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    public Uri uriKeyFile = null;
    public MutableLiveData<String> getPrivateKey() {
        return privateKey;
    }
    public void setPrivateKey(String privateKey) {
        this.privateKey.setValue(privateKey);
    }
    public void copyKeyFile(String name) {
        String fileName = name + SUFFIX_NAME_KEY_FILE;
        String patchFile = "";
        InputStream fis = null;
        OutputStream fos = null;
        try {
            fis = context.getContentResolver().openInputStream(uriKeyFile);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis);
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }

            fos = context.openFileOutput(fileName, MODE_PRIVATE);
            fos.write(stringBuilder.toString().getBytes());

            patchFile = context.getFileStreamPath(fileName).toString();
            this.privateKey.setValue(patchFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            uriKeyFile = null;
            try {
                assert fos != null;
                fos.close();
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
