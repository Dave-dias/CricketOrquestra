package com.example.cricketorquestra;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    final int REQUEST_CODE = 1;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Pede pela permissão de ler os arquivos externos, caso permitido cria um array com eles
        // e chama a atividade principal
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        } else {
            startMainActivity();
            loadSongList();
        }
    }

    // Inicia a atividade principal pelo intent e procura por aquivos de audio no external storage
    private void startMainActivity() {
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Exibe um dialogo caso o usuario não permita o aceso aos arquivos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("The app needs this permission to find your songs," +
                                " if not granted the app will not function properly!")
                        .setTitle("Permission was denied");
                alertDialog.setPositiveButton("Ok, I'll grant it!", (dialog, which) ->
                        ActivityCompat.requestPermissions(SplashScreenActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_CODE));

                alertDialog.setNegativeButton("No, I won't grant it!", (dialog, which) -> {
                    Toast.makeText(SplashScreenActivity.this, "The app will not function properly!", Toast.LENGTH_LONG).show();
                    finish();
                });
                alertDialog.show();
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startMainActivity();
                loadSongList();
            }
        }
    }

    // Chama a função de scannear os arquivos e adiciona eles ao array
    public void loadSongList() {
        Handler postExecute = new Handler(getMainLooper());
        ArrayList<SongClass> files = new ArrayList<>();

        Thread thread = new Thread(() -> {
            ArrayList<File> fileArray = findFiles(Environment.getExternalStorageDirectory());

            for (File singleFile : fileArray) {
                files.add(new SongClass(singleFile.getName().replace(".mp3", "").replace(".wav", "")
                        , singleFile.getPath()));
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            postExecute.post(() -> {
                if (files.size() != 0) {
                    SongCase.setFinalSongList(files);
                    SongCase.setSongList(SongCase.getFinalSongList());
                    SongCase.setQueueList(SongCase.getFinalSongList());

                    Intent fileDiscoveryIntent = new Intent(NotificationManagement.FILE_DISCOVERED);
                    getApplicationContext().sendBroadcast(fileDiscoveryIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No audio file was found", Toast.LENGTH_LONG).show();
                }
            });
        });
        thread.start();
    }

    public ArrayList<File> findFiles(File fileToScan) {
        ArrayList<File> fileArray = new ArrayList<>();
        File[] files = fileToScan.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                // Caso o arquivo seja um diretorio chama ela propria passando o arquivo
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArray.addAll(findFiles(singleFile));
                } else if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    fileArray.add(singleFile);
                }
            }
        }
        return fileArray;
    }
}