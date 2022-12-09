package com.example.cricketorquestra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null){
            switch (intent.getAction()){
                case NotificationManagement.ACTION_PREVIOUS:
                    Intent previousIntent = new Intent(NotificationManagement.ACTION_PREVIOUS);
                    context.sendBroadcast(previousIntent);
                    break;

                case NotificationManagement.ACTION_PLAY_PAUSE:
                    Intent playPauseIntent = new Intent(NotificationManagement.ACTION_PLAY_PAUSE);
                    context.sendBroadcast(playPauseIntent);
                    break;

                case NotificationManagement.ACTION_NEXT:
                    Intent nextIntent = new Intent(NotificationManagement.ACTION_NEXT);
                    context.sendBroadcast(nextIntent);
                    break;

                case NotificationManagement.FILE_DISCOVERED:
                    Intent fileDiscoveryIntent = new Intent(NotificationManagement.FILE_DISCOVERED);
                    context.sendBroadcast(fileDiscoveryIntent);
                    break;
            }
        }
    }
}
