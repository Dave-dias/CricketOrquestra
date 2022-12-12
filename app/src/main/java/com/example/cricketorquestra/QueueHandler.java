package com.example.cricketorquestra;

import java.util.ArrayList;

public interface QueueHandler {
    void setViewsUp();
    void refreshQueue(ArrayList<SongClass> queueList);
    void shuffleActivated();
    void sortQueue();
}
