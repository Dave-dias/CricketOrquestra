package com.example.cricketorquestra;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

public class MusicPlayerFragment extends Fragment implements PlayerHandler{
    ImageView ivCover, ivPausePlay, ivSkipNext, ivSkipPrevious, ivShuffle, ivRepeat;
    TextView tvSongTitle;
    SeekBar sbPlayerBar;

    MusicHandler musicHandler;
    View view;

    static PlayerStates currentPlayerState;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_song_player, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        musicHandler = (MusicHandler) getContext();
        currentPlayerState = PlayerStates.REPEAT_ON;

        ivCover = view.findViewById(R.id.ivCover);
        ivPausePlay = view.findViewById(R.id.ivPausePlay);
        ivSkipNext = view.findViewById(R.id.ivSkipNext);
        ivSkipPrevious = view.findViewById(R.id.ivSkipPrevious);
        ivShuffle = view.findViewById(R.id.ivShuffle);
        ivRepeat = view.findViewById(R.id.ivRepeat);
        tvSongTitle = view.findViewById(R.id.tvSongTitle);
        sbPlayerBar = view.findViewById(R.id.sbPlayerBar);

        setListeners();
    }

    private void setListeners() {
        ivPausePlay.setOnClickListener(v -> playPauseSwitch());

        ivSkipNext.setOnClickListener(v -> nextAudio());

        ivSkipPrevious.setOnClickListener(v -> previousAudio());

        ivShuffle.setOnClickListener(v -> shuffleSwitch(currentPlayerState));

        ivRepeat.setOnClickListener(v -> repeatSwitch(currentPlayerState));

        sbPlayerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicHandler.onProgressChanged(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void setViewsUp() {
        updateSeekbar();
        tvSongTitle.setText(CurrentMusic.getThisObject().getTitle());

        if (CurrentMusic.isIsPlaying()) {
            ivPausePlay.setImageResource(R.drawable.ic_play_circle);
        } else {
            ivPausePlay.setImageResource(R.drawable.ic_pause_circle);
        }
    }

    @Override
    public void playPauseSwitch() {
        if (CurrentMusic.isIsPlaying()) {
            ivPausePlay.setImageResource(R.drawable.ic_pause_circle);
            CurrentMusic.setIsPlaying(false);
        } else {
            ivPausePlay.setImageResource(R.drawable.ic_play_circle);
            CurrentMusic.setIsPlaying(true);
        }
        musicHandler.onPlayPauseSwitch();
    }

    private void nextAudio() {
        musicHandler.onNextAudioSelected();
    }

    private void previousAudio() {
        musicHandler.onPreviousAudioSelected();
    }

    @Override
    public void shuffleSwitch(PlayerStates currentPlayerState) {
        if (currentPlayerState == PlayerStates.SHUFFLE_ON) {
            MusicPlayerFragment.currentPlayerState = PlayerStates.SHUFFLE_OFF;
            ivShuffle.setImageResource(R.drawable.ic_shuffle);
        } else {
            MusicPlayerFragment.currentPlayerState = PlayerStates.SHUFFLE_ON;
            ivShuffle.setImageResource(R.drawable.ic_shuffle_on);
        }

        ivRepeat.setImageResource(R.drawable.ic_repeat);
        musicHandler.onShuffleSwitch();
    }

    @Override
    public void repeatSwitch(PlayerStates currentPlayerState) {
        ivShuffle.setImageResource(R.drawable.ic_shuffle);
        if (currentPlayerState == PlayerStates.REPEAT_ON) {
            MusicPlayerFragment.currentPlayerState = PlayerStates.REPEAT_ONE_ON;
            ivRepeat.setImageResource(R.drawable.ic_repeat_one_on);
        } else {
            MusicPlayerFragment.currentPlayerState = PlayerStates.REPEAT_ON;
            ivRepeat.setImageResource(R.drawable.ic_repeat);
        }
        musicHandler.onRepeatSwitch();
    }

    @Override
    public void updateSeekbar() {
        Thread timer = new Thread(() -> {
            int totalProgress = musicHandler.getTotalProgress();
            int currentProgress = 0;

            while (currentProgress < totalProgress) {
                try {
                    Thread.sleep(500);
                    currentProgress = musicHandler.getCurrentProgress();
                    sbPlayerBar.setProgress(currentProgress);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        sbPlayerBar.setMax(musicHandler.getTotalProgress());
        timer.start();
    }
}