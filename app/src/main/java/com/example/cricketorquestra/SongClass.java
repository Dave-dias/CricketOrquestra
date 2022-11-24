package com.example.cricketorquestra;

public class SongClass {
    String Title;
    String sourceFolder;

    public SongClass(String title, String sourceFolder) {
        Title = title;
        this.sourceFolder = sourceFolder;
    }

    public String getTitle() {
        return Title;
    }

    public String getSourceFolder() {
        return sourceFolder;
    }
}
