package com.example.cricketorquestra;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Application extends android.app.Application{
    final static List<SongClass> songList = loadSongList();
    static ArrayList<SongClass> sortedList;
    static ArrayList<SongClass> queueList;

    static Context context;

    // Chama a função de scannear os arquivos e adiciona eles ao array
    public static ArrayList<SongClass> loadSongList() {
        ArrayList<SongClass> List = new ArrayList<>();

        ArrayList<File> fileArray = findFiles(Environment.getExternalStorageDirectory());

        for (File singleFile : fileArray) {
            List.add(new SongClass(singleFile.getName().replace(".mp3", "").replace(".wav", "")
                    , singleFile.getPath()));
        }

        return List;
    }

    public static ArrayList<File> findFiles(File fileToScan) {
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
