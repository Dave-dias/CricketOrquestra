package com.example.cricketorquestra;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotificationManagement {
    final static String CHANNEL_ID = "Media_Player";
    final static String ACTION_PREVIOUS = "Previous";
    final static String ACTION_PLAY_PAUSE = "Play/Pause";
    final static String ACTION_NEXT = "Next";
    final static String FILE_DISCOVERED = "Discovered";
    final static String PROGRESS_UPDATE = "Progress update";

    NotificationManager manager;
    Context context;

    NotificationManagement(Context context){
        this.context = context;
    }

    //Cria o canal de notificação
    public void createNotificationChannel(){
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                "Media player", NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription("Media player notification channel");
        manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(notificationChannel);
    }

    // Cria notificação, seta ações e dá display nela
    public void showNotification() {
        Intent contentIntent = new Intent(context, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context,
                0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        Intent previousIntent = new Intent(context, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context,
                0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        Intent playPauseIntent = new Intent(context, NotificationReceiver.class)
                .setAction(ACTION_PLAY_PAUSE);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(context,
                0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(context, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context,
                0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle("Playing now...")
                .setContentText(CurrentMusic.getThisObject().getTitle())
                .setContentIntent(contentPendingIntent)
                .addAction(R.drawable.ic_skip_previous, "Previous", previousPendingIntent)
                .addAction(R.drawable.ic_play_circle, "Play/Pause", playPausePendingIntent)
                .addAction(R.drawable.ic_skip_next, "Next", nextPendingIntent)
                .setOnlyAlertOnce(true);

        manager.notify(0, builder.build());
    }

    public void cancelNotification (){
        manager.cancel(0);
    }
}
