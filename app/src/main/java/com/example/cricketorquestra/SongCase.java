package com.example.cricketorquestra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongCase {
    static ArrayList<SongClass> songList = new ArrayList<>();
    static ArrayList<SongClass> sortedList = new ArrayList<>();
    static ArrayList<SongClass> queueList = new ArrayList<>();
    static List<SongClass> finalSongList = Collections.emptyList();

    public static ArrayList<SongClass> getSongList() {
        songList = getFinalSongList();
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

    public static ArrayList<SongClass> getFinalSongList() {
        return new ArrayList<>(finalSongList);
    }

    public static void setFinalSongList(List<SongClass> finalSongList) {
        SongCase.finalSongList = Collections.unmodifiableList(finalSongList);
    }
}
