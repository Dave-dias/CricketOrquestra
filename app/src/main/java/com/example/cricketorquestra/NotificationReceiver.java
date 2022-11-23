package com.example.cricketorquestra;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null){
            switch (intent.getAction()){
                case SplashScreenActivity.ACTION_PREVIOUS:
                    Intent previousIntent = new Intent(SplashScreenActivity.ACTION_PREVIOUS);
                    context.sendBroadcast(previousIntent);
                    break;

                case SplashScreenActivity.ACTION_PLAY_PAUSE:
                    Intent playPauseIntent = new Intent(SplashScreenActivity.ACTION_PLAY_PAUSE);
                    context.sendBroadcast(playPauseIntent);
                    break;

                case SplashScreenActivity.ACTION_NEXT:
                    Intent nextIntent = new Intent(SplashScreenActivity.ACTION_NEXT);
                    context.sendBroadcast(nextIntent);
                    break;
            }
        }
    }
}
