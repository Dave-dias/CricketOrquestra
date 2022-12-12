package com.example.cricketorquestra;

import java.util.ArrayList;

public class SongCase {
    static ArrayList<SongClass> songList;
    static ArrayList<SongClass> sortedList;
    static ArrayList<SongClass> queueList;
    static ArrayList<SongClass> finalSongList;

    public static ArrayList<SongClass> getSongList() {
        songList = finalSongList;
        return songList;
    }

    public static void setSongList(ArrayList<SongClass> songList) {
        SongCase.songList = songList;
    }

    public static ArrayList<SongClass> getSortedList() {
        return sortedList;
    }

    public static void setSortedList(ArrayList<SongClass> sortedList) {
        SongCase.sortedList = sortedList;
    }

    public static ArrayList<SongClass> getQueueList() {
        return queueList;
    }

    public static void setQueueList(ArrayList<SongClass> queueList) {
        SongCase.queueList = queueList;
    }

    public static void setFinalSongList(ArrayList<SongClass> finalSongList) {
        SongCase.finalSongList = finalSongList;
    }
}
