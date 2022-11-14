package com.example.cricketorquestra;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements MusicHandler {
    enum displayedFragment {SONG_LIBRARY, MUSIC_PLAYER}
    ImageView ivCover, ivPausePlay, ivSkipNext, ivSkipPrevious, ivShuffle, ivRepeat;
    TextView tvSongTitle, tvNavBarLibrary, tvNavBarPlayer;
    SeekBar sbPlayerBar;

    Fragment MusicPlayerFragment, SongLibraryFragment;
    FragmentManager fragmentManager;
    MediaPlayer mediaPlayer;
    Timer timer;

    static ArrayList<SongClass> songList;
    ArrayList<Integer> playedSongs;
    PlayerStates currentState;
    int currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playedSongs = new ArrayList<>();
        mediaPlayer = MediaPlayer.create(this, songList.get(0).getSourceFolder());
        currentState = PlayerStates.REPEAT_OFF;

        fragmentManager = getSupportFragmentManager();
        SongLibraryFragment = new SongLibraryFragment();
        MusicPlayerFragment = new MusicPlayerFragment();

        fragmentManager.beginTransaction()
                .add(R.id.FragmentContainer, SongLibraryFragment)
                .add(R.id.FragmentContainer, MusicPlayerFragment)
                .hide(SongLibraryFragment)
                .show(MusicPlayerFragment)
                .commitNow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOnViews();
        setMusicPlayerUp();
    }

    private void setOnViews(){
        tvNavBarLibrary = findViewById(R.id.tvNavLibrary);
        tvNavBarPlayer = findViewById(R.id.tvNavPlayer);
        ivCover = findViewById(R.id.ivCover);
        ivPausePlay = findViewById(R.id.ivPausePlay);
        ivSkipNext = findViewById(R.id.ivSkipNext);
        ivSkipPrevious = findViewById(R.id.ivSkipPrevious);
        ivShuffle = findViewById(R.id.ivShuffle);
        ivRepeat = findViewById(R.id.ivRepeat);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        sbPlayerBar = findViewById(R.id.sbPlayerBar);

        setOnListeners();
    }

    private void setOnListeners() {
        tvNavBarPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSwitch(displayedFragment.MUSIC_PLAYER);
            }
        });

        tvNavBarLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSwitch(displayedFragment.SONG_LIBRARY);
            }
        });


        ivPausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseSwitch();
            }
        });

        ivSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextAudio();
            }
        });

        ivSkipPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousAudio();
            }
        });

        ivShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleSwitch();
            }
        });

        ivRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatSwitch();
            }
        });

        sbPlayerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null){
                    if (fromUser){
                        mediaPlayer.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            });
    }


    void fragmentSwitch (displayedFragment display){
        switch (display){
            case SONG_LIBRARY:
                fragmentManager.beginTransaction()
                        .show(SongLibraryFragment)
                        .hide(MusicPlayerFragment)
                        .commitNow();
                break;
            case MUSIC_PLAYER:
                fragmentManager.beginTransaction()
                        .show(MusicPlayerFragment)
                        .hide(SongLibraryFragment)
                        .commitNow();
                setMusicPlayerUp();
                break;
        }
    }

    private void setMediaCompleteListener(){
        mediaPlayer.setOnCompletionListener(mp -> {
            switch (currentState){
                case REPEAT_OFF:
                case SHUFFLE_OFF:
                    mediaPlayer.reset();
                    mediaPlayer = MediaPlayer.create(this, songList.get(currentSong).getSourceFolder());
                    ivPausePlay.setImageResource(R.drawable.ic_play_circle);
                    break;
                case SHUFFLE_ON:
                    playedSongs.add(currentSong);
                    if (playedSongs.size() == songList.size()){
                        mediaPlayer.reset();
                        mediaPlayer = MediaPlayer.create(this, songList.get(currentSong).getSourceFolder());
                        ivPausePlay.setImageResource(R.drawable.ic_play_circle);
                    } else {
                        int randSong = sortRandomSong();
                        onMusicSelected(randSong);
                        currentSong = randSong;
                        setMusicPlayerUp();
                    }
                    break;
                case REPEAT_ON:
                    nextAudio();
                    break;
                case REPEAT_ONE_ON:
                    mediaPlayer.reset();
                    onMusicSelected(currentSong);
                    break;
            }
        });
    }

    @Override
    public void onMusicSelected(int index) {
        currentSong = index;
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(this, songList.get(index).getSourceFolder());
        setMediaCompleteListener();
        playPauseSwitch();
    }

    public void playPauseSwitch() {
        if (mediaPlayer != null){
            if (!mediaPlayer.isPlaying()){
                mediaPlayer.start();
                ivPausePlay.setImageResource(R.drawable.ic_pause_circle);
                updateSeekbar();
            } else {
                mediaPlayer.pause();
                ivPausePlay.setImageResource(R.drawable.ic_play_circle);
            }
        }
    }

    public void shuffleSwitch() {
        if (currentState == PlayerStates.SHUFFLE_ON) {
            currentState = PlayerStates.SHUFFLE_OFF;
            ivShuffle.setImageResource(R.drawable.ic_shuffle);
        } else {
            currentState = PlayerStates.SHUFFLE_ON;
            ivShuffle.setImageResource(R.drawable.ic_shuffle_on);
            ivRepeat.setImageResource(R.drawable.ic_repeat);
        }
    }

    public void repeatSwitch() {
        ivShuffle.setImageResource(R.drawable.ic_shuffle);
        switch (currentState){
            case REPEAT_ON:
                currentState = PlayerStates.REPEAT_ONE_ON;
                ivRepeat.setImageResource(R.drawable.ic_repeat_one_on);
                break;
            case REPEAT_ONE_ON:
                currentState = PlayerStates.REPEAT_OFF;
                ivRepeat.setImageResource(R.drawable.ic_repeat);
                break;
            default:
                currentState = PlayerStates.REPEAT_ON;
                ivRepeat.setImageResource(R.drawable.ic_repeat_on);
                break;
        }
    }

    public void previousAudio() {
        if (currentState.equals(PlayerStates.SHUFFLE_ON)){
            onMusicSelected(sortRandomSong());
        } else if (currentSong != 0) {
            onMusicSelected(currentSong - 1);
        } else {
            onMusicSelected(currentSong);
        }
        setMusicPlayerUp();
    }

    public void nextAudio() {
        if (currentState.equals(PlayerStates.SHUFFLE_ON)){
            onMusicSelected(sortRandomSong());
        } else if (currentSong != songList.size() -1) {
            onMusicSelected(currentSong + 1);
        } else {
            onMusicSelected(currentSong);
        }
        setMusicPlayerUp();
    }

    private int sortRandomSong() {
        while (true){
            int index = (int) Math.floor((Math.random() * (songList.size() - 1) - 0 + 1) + 0);
            if (!playedSongs.contains(index)){
                if (playedSongs.size() == songList.size() - 1){
                 shuffleSwitch();
                 playedSongs.clear();
                }
                return index;
            }
        }
    }
    
    public void updateSeekbar() {
        sbPlayerBar.setMax(mediaPlayer.getDuration());

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try{
                    sbPlayerBar.setProgress(mediaPlayer.getCurrentPosition());
                } catch (Exception e){
                }
            }
        },0, 1000);
    }

    private void setMusicPlayerUp() {
        tvSongTitle.setText(songList.get(currentSong).getTitle());
        setMediaCompleteListener();
        updateSeekbar();

        if (!mediaPlayer.isPlaying()){
            ivPausePlay.setImageResource(R.drawable.ic_play_circle);
        } else {
            ivPausePlay.setImageResource(R.drawable.ic_pause_circle);
        }

        switch (currentState){
            case REPEAT_ON:
                ivRepeat.setImageResource(R.drawable.ic_repeat_on);
                break;
            case REPEAT_ONE_ON:
                ivRepeat.setImageResource(R.drawable.ic_repeat_one_on);
                break;
            case SHUFFLE_ON:
                ivShuffle.setImageResource(R.drawable.ic_shuffle_on);
                break;
        }
    }
}