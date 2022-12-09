package com.example.cricketorquestra;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Application extends android.app.Application{
    static Context context;
    static ArrayList<SongClass> sortedList;
    static ArrayList<SongClass> songList;
    static ArrayList<SongClass> queueList;

    @Override
    public void onCreate() {
        super.onCreate();

        loadSongList();
    }

    // Chama a função de scannear os arquivos e adiciona eles ao array
    public void loadSongList() {
        Executor backgroundThread = Executors.newSingleThreadExecutor();
        Handler postExecute = new Handler(Looper.getMainLooper());
        songList = new ArrayList<>();

        backgroundThread.execute(() -> {
            ArrayList<File> fileArray = findFiles(Environment.getExternalStorageDirectory());

            for (File singleFile : fileArray) {
                songList.add(new SongClass(singleFile.getName().replace(".mp3", "").replace(".wav", "")
                        , singleFile.getPath()));
            }
            postExecute.post(() -> {
                if (songList.size() != 0) {
                    queueList = songList;

                    Intent fileDiscoveryIntent = new Intent(NotificationManagement.FILE_DISCOVERED);
                    this.sendBroadcast(fileDiscoveryIntent);
                } else {
                    Toast.makeText(this, "No audio file was found", Toast.LENGTH_LONG).show();
                }
            });
        });
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
