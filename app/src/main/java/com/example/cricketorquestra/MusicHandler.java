package com.example.cricketorquestra;

public interface MusicHandler {
    void onSelectedMusicLibrary(int index);
    void onSelectedQueue(int index);
    void onMusicStopped();
    void onPlayPauseSwitch();
    void onRepeatSwitch();
    void onShuffleSwitch();
    void onNextAudioSelected();
    void onPreviousAudioSelected();
    void onProgressChanged(int progress);
    int getCurrentProgress();
    int getTotalProgress();
}
