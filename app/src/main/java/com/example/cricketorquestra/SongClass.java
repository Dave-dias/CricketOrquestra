package com.example.cricketorquestra;

import android.net.Uri;

import java.net.URI;

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

    public void setTitle(String title) {
        Title = title;
    }

    public Uri getSourceFolder() {
        return sourceFolder;
    }

    public void setSourceFolder(Uri sourceFolder) {
        this.sourceFolder = sourceFolder;
    }
}
