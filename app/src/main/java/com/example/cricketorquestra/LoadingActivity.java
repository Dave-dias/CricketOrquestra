package com.example.cricketorquestra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class LoadingActivity extends AppCompatActivity {
    ArrayList<SongClass> songList;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 0);
        songList = loadSongList();

        if (!songList.isEmpty()){
            intent.putExtra("wasFound", true);
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No audio archive was found!", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    public ArrayList<SongClass> loadSongList() {
        Toast.makeText(this,"Looking audio archives...", Toast.LENGTH_SHORT).show();
        ArrayList<File> fileArray = findFiles(Environment.getExternalStorageDirectory());
        ArrayList<SongClass> songList = new ArrayList<>();

        for (File singleFile: fileArray){
            songList.add(new SongClass(singleFile.getName().replace(".mp3", "").replace(".wav","")
                    , Uri.parse(singleFile.getPath())));
        }

        MainActivity.songList = songList;
        return songList;
    }


    private ArrayList<File> findFiles (File fileToScan){
        ArrayList<File> fileArray = new ArrayList<>();
        File[] files = fileToScan.listFiles();

        if (files != null) {
            for (File singleFile: files){
                if (singleFile.isDirectory() && !singleFile.isHidden()){
                    fileArray.addAll(findFiles(singleFile));
                } else if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")){
                    fileArray.add(singleFile);
                }
            }
        }
        return fileArray;
    }
}