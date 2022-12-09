package com.example.cricketorquestra;

public interface PlayerHandler {
    void setViewsUp();
    void playPauseSwitch();
    void updateSeekbar();
    void shuffleSwitch(PlayerStates currentPlayerState);
    void repeatSwitch(PlayerStates currentPlayerState);
}
