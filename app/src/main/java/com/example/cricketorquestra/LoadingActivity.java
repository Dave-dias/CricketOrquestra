package com.example.cricketorquestra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class LoadingActivity extends AppCompatActivity {
    final int REQUEST_CODE = 1;
    ArrayList<SongClass> songList;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                alertDialog.setMessage("The app needs this permission to find your songs," +
                                " if not granted the app will not function properly!")
                                .setTitle("Permission was denied");
                alertDialog.setPositiveButton("Ok, I'll grant it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_CODE);
                    }
                });

                alertDialog.setNegativeButton("No, I won't grant it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LoadingActivity.this, "The app will not function properly!", Toast.LENGTH_LONG).show();
                    }
                });


            }
        }
    }

    public ArrayList<SongClass> loadSongList() {
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