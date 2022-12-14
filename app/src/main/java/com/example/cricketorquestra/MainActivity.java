package com.example.cricketorquestra;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements MusicHandler, DisplayHandler{
    TextView tvNavBarLibrary, tvNavBarPlayer, tvNavBarQueue;
    Drawable drwPlayer, drwLibrary, drwQueue;
    ProgressBar pbLoadingFiles;

    Fragment MusicPlayerFrag, SongLibraryFrag, QueueFrag;
    FragmentManager fragmentManager;
    PlayerHandler playerHandler;
    QueueHandler queueHandler;
    LibraryHandler libraryHandler;
    NotificationManagement notificationManagement;
    MediaPlayerService mediaService;
    Intent mediaServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_main);

        notificationManagement = new NotificationManagement(this);
        notificationManagement.createNotificationChannel();

        fragmentManager = getSupportFragmentManager();
        SongLibraryFrag = new SongLibraryFragment();
        MusicPlayerFrag = new MusicPlayerFragment();
        QueueFrag = new QueueFragment();

        playerHandler = (PlayerHandler) MusicPlayerFrag;
        libraryHandler = (LibraryHandler) SongLibraryFrag;
        queueHandler = (QueueHandler) QueueFrag;

        fragmentManager.beginTransaction()
                .add(R.id.FragmentContainer, SongLibraryFrag)
                .add(R.id.FragmentContainer, MusicPlayerFrag)
                .add(R.id.FragmentContainer, QueueFrag)
                .hide(SongLibraryFrag)
                .hide(QueueFrag)
                .show(MusicPlayerFrag)
                .commitNow();

        actionBarSwitch(true);
        setReceiverUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setOnViews();

        mediaServiceIntent = new Intent(this, MediaPlayerService.class);
        bindService(mediaServiceIntent, connection,Context.BIND_AUTO_CREATE);

        drwLibrary = tvNavBarLibrary.getCompoundDrawablesRelative()[3];
        drwPlayer = tvNavBarPlayer.getCompoundDrawablesRelative()[3];
        drwQueue = tvNavBarQueue.getCompoundDrawablesRelative()[3];
        drwLibrary.setBounds(tvNavBarLibrary.getCompoundDrawablesRelative()[3].getBounds());
        drwPlayer.setBounds(tvNavBarPlayer.getCompoundDrawablesRelative()[3].getBounds());
        drwQueue.setBounds(tvNavBarQueue.getCompoundDrawablesRelative()[3].getBounds());

        onConfigurationChanged(this.getResources().getConfiguration());
    }

    @Override
    protected void onDestroy() {
        notificationManagement.cancelNotification();

        stopService(mediaServiceIntent);
        unbindService(connection);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getSupportActionBar() != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                actionBarSwitch(false);
                tvNavBarPlayer.setCompoundDrawables(null, null, null, null);
                tvNavBarLibrary.setCompoundDrawables(null, null, null, null);
                tvNavBarQueue.setCompoundDrawables(null, null,null, null);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                actionBarSwitch(true);
                tvNavBarPlayer.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null, null, null, drwPlayer);
                tvNavBarLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null, null, null, drwLibrary);
                tvNavBarQueue.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null, null, null, drwQueue);
            }
        }
    }

    @Override
    public void onBackPressed() {
        fragmentSwitch(DisplayedFragment.MUSIC_PLAYER);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mediaService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };


    private void setReceiverUp() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("Play/Pause");
        filter.addAction("Previous");
        filter.addAction("Next");
        filter.addAction("Discovered");
        filter.addAction("Prepared");
        filter.addAction("Stopped");

        BroadcastReceiver MainActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    switch (intent.getAction()) {
                        case NotificationManagement.ACTION_PREVIOUS:
                            onPreviousAudioSelected();
                            break;

                        case NotificationManagement.ACTION_PLAY_PAUSE:
                            playerHandler.playPauseSwitch();
                            break;

                        case NotificationManagement.ACTION_NEXT:
                            onNextAudioSelected();
                            break;

                        case NotificationManagement.ON_PREPARED:
                            setPlayerUp();
                            setQueueUp();
                            break;

                        case NotificationManagement.ON_STOP:
                            onMusicStopped();
                            break;

                        case NotificationManagement.FILE_DISCOVERED:
                            pbLoadingFiles.setVisibility(View.INVISIBLE);
                            libraryHandler.refreshLibrary();
                            queueHandler.refreshQueue(SongCase.getSongList());
                            queueHandler.sortQueue();
                            break;
                    }
                }
            }
        };

        registerReceiver(MainActivityReceiver, filter);
    }

    private void actionBarSwitch(boolean isVisible) {
        if (getSupportActionBar() != null){
            if (isVisible){
                getSupportActionBar().show();
            } else {
                getSupportActionBar().hide();
            }

            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher_foreground);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setOnViews() {
        tvNavBarLibrary = findViewById(R.id.tvNavLibrary);
        tvNavBarQueue = findViewById(R.id.tvNavQueue);
        tvNavBarPlayer = findViewById(R.id.tvNavPlayer);
        pbLoadingFiles = findViewById(R.id.pbLoadingFiles);

        tvNavBarPlayer.setOnClickListener(v -> fragmentSwitch(DisplayedFragment.MUSIC_PLAYER));
        tvNavBarLibrary.setOnClickListener(v -> fragmentSwitch(DisplayedFragment.SONG_LIBRARY));
        tvNavBarQueue.setOnClickListener(v -> fragmentSwitch(DisplayedFragment.QUEUE_LIST));
    }

    void fragmentSwitch(DisplayedFragment display) {
            switch (display) {
                case SONG_LIBRARY:
                    actionBarSwitch(true);
                    fragmentManager.beginTransaction()
                            .show(SongLibraryFrag)
                            .hide(MusicPlayerFrag)
                            .hide(QueueFrag)
                            .commitNow();
                    break;
                    
                case MUSIC_PLAYER:
                    actionBarSwitch(true);
                    fragmentManager.beginTransaction()
                            .show(MusicPlayerFrag)
                            .hide(SongLibraryFrag)
                            .hide(QueueFrag)
                            .commitNow();
                    break;

                case QUEUE_LIST:
                    actionBarSwitch(false);
                    fragmentManager.beginTransaction()
                            .show(QueueFrag)
                            .hide(MusicPlayerFrag)
                            .hide(SongLibraryFrag)
                            .commitNow();
                    break;
            }
    }


    @Override
    public void onSelectedMusicLibrary(int index) {
        queueHandler.refreshQueue(SongCase.getSongList());
        onSelectedQueue(index);
    }

    @Override
    public void onSelectedQueue(int index) {
        CurrentMusic.setThisObject(SongCase.getQueueList().get(index));
        CurrentMusic.setIsPlaying(false);
        CurrentMusic.setIndex(index);

        mediaServiceIntent.putExtra("Audio File",
                CurrentMusic.getThisObject().getSourceFolder());
        startService(mediaServiceIntent);

        notificationManagement.showNotification();
    }

    @Override
    public void setQueueUp() {queueHandler.setViewsUp();}

    @Override
    public void setPlayerUp() {playerHandler.setViewsUp();}

    @Override
    public void setLibraryUp() {libraryHandler.refreshLibrary();}

    @Override
    public void onMusicStopped() {onNextAudioSelected();}

    @Override
    public void onPlayPauseSwitch() {mediaService.playPauseMedia();}

    @Override
    public void onNextAudioSelected() {
        if (CurrentMusic.getIndex() == SongCase.getQueueList().size() - 1) {
            onSelectedQueue(CurrentMusic.getIndex());
        } else if (MusicPlayerFragment.currentPlayerState == PlayerStates.REPEAT_ONE_ON) {
            onSelectedQueue(CurrentMusic.getIndex());
        } else {
            onSelectedQueue(CurrentMusic.getIndex() + 1);
        }
    }

    @Override
    public void onPreviousAudioSelected() {
        if (CurrentMusic.getIndex() == 0) {
            onSelectedQueue(0);
        } else if (MusicPlayerFragment.currentPlayerState == PlayerStates.REPEAT_ONE_ON) {
            onSelectedQueue(CurrentMusic.getIndex());
        } else {
            onSelectedQueue(CurrentMusic.getIndex() - 1);
        }
    }

    @Override
    public void onRepeatSwitch() {
        mediaService.switchLoop(MusicPlayerFragment.currentPlayerState == PlayerStates.REPEAT_ONE_ON);
    }

    @Override
    public void onShuffleSwitch() {
        if (MusicPlayerFragment.currentPlayerState == PlayerStates.SHUFFLE_ON){
            queueHandler.refreshQueue(SongCase.getSortedList());
            queueHandler.shuffleActivated();
        } else {
            queueHandler.refreshQueue(SongCase.getSongList());
        }

        mediaService.switchLoop(false);
        queueHandler.sortQueue();
    }

    @Override
    public void onProgressChanged(int progress) { mediaService.seekToProgress(progress);}

    @Override
    public int getCurrentProgress(){return mediaService.getProgress();}

    @Override
    public int getTotalProgress(){return mediaService.getTotalProgress();}
}