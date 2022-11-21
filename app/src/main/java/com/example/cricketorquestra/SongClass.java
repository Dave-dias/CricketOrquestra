package com.example.cricketorquestra;

import android.net.Uri;

public class SongClass {
    String Title;
    Uri sourceFolder;

    public SongClass(String title, Uri sourceFolder) {
        Title = title;
        this.sourceFolder = sourceFolder;
    }

    public String getTitle() {
        return Title;
    }

    public Uri getSourceFolder() {
        return sourceFolder;
    }
}
