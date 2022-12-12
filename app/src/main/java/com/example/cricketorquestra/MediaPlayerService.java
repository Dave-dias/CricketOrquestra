package com.example.cricketorquestra;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private final IBinder binder = new LocalBinder();
    private MediaPlayer mediaPlayer;

    public MediaPlayerService (){
    }

    public class LocalBinder extends Binder {
        MediaPlayerService getService(){
            return MediaPlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String audioFile = intent.getStringExtra("Audio File");
        mediaPlayer.reset();

        if (!mediaPlayer.isPlaying()){
            try {
                mediaPlayer.setDataSource(audioFile);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        return START_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        Intent intent = new Intent(NotificationManagement.ON_STOP);
        sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what){
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK",
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA_ERROR_UNKNOWN",
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (!mediaPlayer.isPlaying()){
            Intent intent = new Intent(NotificationManagement.ON_PREPARED);
            sendBroadcast(intent);
            mediaPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    public void playPauseMedia (){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    public int getProgress(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getTotalProgress(){
        return mediaPlayer.getDuration();
    }

    public void seekToProgress(int progress){
        mediaPlayer.seekTo(progress);
    }

    public void switchLoop (boolean isOn){
        mediaPlayer.setLooping(isOn);
    }
}
