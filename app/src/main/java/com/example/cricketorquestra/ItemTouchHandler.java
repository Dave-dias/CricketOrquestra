package com.example.cricketorquestra;

public interface ItemTouchHandler {
    void onDrag (int oldPosition, int newPosition);
    void onSwipe (int position);
}
