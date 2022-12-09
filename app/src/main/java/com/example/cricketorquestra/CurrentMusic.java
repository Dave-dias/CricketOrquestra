package com.example.cricketorquestra;

public class CurrentMusic {
    public static int index;
    public static boolean isPlaying = false;
    public static SongClass thisObject;

    public static int getIndex() {
        return index;
    }

    public static void setIndex(int index) {
        CurrentMusic.index = index;
    }

    public static boolean isIsPlaying() {
        return isPlaying;
    }

    public static void setIsPlaying(boolean isPlaying) {
        CurrentMusic.isPlaying = isPlaying;
    }

    public static SongClass getThisObject() {
        return thisObject;
    }

    public static void setThisObject(SongClass thisObject) {
        CurrentMusic.thisObject = thisObject;
    }
}
