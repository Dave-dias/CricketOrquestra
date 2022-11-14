package com.example.cricketorquestra;

import java.util.ArrayList;

public interface MusicHandler {
    ArrayList<SongClass> loadSongList();
    void onMusicSelected(int index);
}
