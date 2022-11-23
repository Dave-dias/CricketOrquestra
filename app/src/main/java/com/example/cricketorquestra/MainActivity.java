package com.example.cricketorquestra;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MusicHandler,
        MediaPlayer.OnCompletionListener {

    enum displayedFragment {SONG_LIBRARY, MUSIC_PLAYER}
    ImageView ivCover, ivPausePlay, ivSkipNext, ivSkipPrevious, ivShuffle, ivRepeat;
    TextView tvSongTitle, tvNavBarLibrary, tvNavBarPlayer;
    SeekBar sbPlayerBar;

    Fragment MusicPlayerFragment, SongLibraryFragment;
    FragmentManager fragmentManager;
    MediaPlayer mediaPlayer;

    static ArrayList<SongClass> songList;
    ArrayList<Integer> playedSongs;
    PlayerStates currentState;
    int currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_main);

        // Setando os fragementos, arrays e criando o media player
        songList = SplashScreenActivity.songList;
        playedSongs = new ArrayList<>();
        mediaPlayer = MediaPlayer.create(this, songList.get(0).getSourceFolder());
        currentState = PlayerStates.REPEAT_ON;

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

    // Caso tente setar no onCreate, as views não são encontradas
    // pois não foram criadas ainda
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
        tvNavBarPlayer.setOnClickListener(v -> fragmentSwitch(displayedFragment.MUSIC_PLAYER));

        tvNavBarLibrary.setOnClickListener(v -> fragmentSwitch(displayedFragment.SONG_LIBRARY));

        ivPausePlay.setOnClickListener(v -> playPauseSwitch());

        ivSkipNext.setOnClickListener(v -> nextAudio());

        ivSkipPrevious.setOnClickListener(v -> previousAudio());

        ivShuffle.setOnClickListener(v -> shuffleSwitch());

        ivRepeat.setOnClickListener(v -> repeatSwitch());

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
                break;
        }
    }

    // Checando o estado do player para saber qual o proximo passo
    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (currentState){
            case SHUFFLE_OFF:
                clearMediaPlayer();
                mediaPlayer = MediaPlayer.create(this,
                        songList.get(currentSong).getSourceFolder());
                ivPausePlay.setImageResource(R.drawable.ic_play_circle);
                break;
            case SHUFFLE_ON:
                if (playedSongs.size() == songList.size()){
                    clearMediaPlayer();
                    mediaPlayer = MediaPlayer.create(this,
                            songList.get(currentSong).getSourceFolder());
                    ivPausePlay.setImageResource(R.drawable.ic_play_circle);
                } else {
                    int randSong = sortRandomSong();
                    onMusicSelected(randSong);
                    currentSong = randSong;
                }
                setMusicPlayerUp();
                break;
            case REPEAT_ON:
                nextAudio();
                break;
        }
    }

    // Crio e dou start no audio selecionado
    @Override
    public void onMusicSelected(int index) {
        currentSong = index;
        clearMediaPlayer();
        mediaPlayer = MediaPlayer.create(this, songList.get(index).getSourceFolder());
        mediaPlayer.start();
        setMusicPlayerUp();
    }

    private void clearMediaPlayer(){
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void playPauseSwitch() {
        if (mediaPlayer != null){
            if (!mediaPlayer.isPlaying()){
                mediaPlayer.start();
                ivPausePlay.setImageResource(R.drawable.ic_pause_circle);
            } else {
                mediaPlayer.pause();
                ivPausePlay.setImageResource(R.drawable.ic_play_circle);
            }
        }
    }

    // Alterna os estados do player
    public void shuffleSwitch() {
        if (currentState == PlayerStates.SHUFFLE_ON) {
            currentState = PlayerStates.SHUFFLE_OFF;
            ivShuffle.setImageResource(R.drawable.ic_shuffle);
        } else {
            currentState = PlayerStates.SHUFFLE_ON;
            ivShuffle.setImageResource(R.drawable.ic_shuffle_on);
            ivRepeat.setImageResource(R.drawable.ic_repeat);
            mediaPlayer.setLooping(false);
        }
    }

    // Alterna os estados do playe
    public void repeatSwitch() {
        ivShuffle.setImageResource(R.drawable.ic_shuffle);
        if (currentState == PlayerStates.REPEAT_ON) {
            currentState = PlayerStates.REPEAT_ONE_ON;
            ivRepeat.setImageResource(R.drawable.ic_repeat_one_on);
            mediaPlayer.setLooping(true);
        } else {
            currentState = PlayerStates.REPEAT_ON;
            ivRepeat.setImageResource(R.drawable.ic_repeat);
            mediaPlayer.setLooping(false);
        }
    }

    // Verifica se existe audio anterior, se existe seleciona o audio
    public void previousAudio() {
        // Caso o shuffle esteja ativado chama um novo audio aleatorio
        if (currentState.equals(PlayerStates.SHUFFLE_ON)){
            onMusicSelected(sortRandomSong());
        } else if (currentSong != 0) {
            onMusicSelected(currentSong - 1);
        } else {
            onMusicSelected(currentSong);
        }
        setMusicPlayerUp();
    }

    // Verifica se existe audio posterior, se existe seleciona o audio
    public void nextAudio() {
        // Caso o shuffle esteja ativado chama um o metodo de sorteio
        if (currentState.equals(PlayerStates.SHUFFLE_ON)){
            onMusicSelected(sortRandomSong());
        } else if (currentSong != songList.size() -1) {
            onMusicSelected(currentSong + 1);
        } else {
            onMusicSelected(0);
        }
        setMusicPlayerUp();
    }

    private int sortRandomSong() {
        while (true){
            // Gera um numero aleatorio dentro do intervado entre 0 e o tamanho do array de musicas
            int index = (int) Math.floor((Math.random() * (songList.size() - 1) - 0 + 1) + 0);
            // Verifica se a musica não foi tocada desde que o shuffle foi ativado
            if (!playedSongs.contains(index)){
                playedSongs.add(index);
                // Caso já tenha tocado todas as musicas, desativa o shuffle e reinicia o array
                if (playedSongs.size() == songList.size() - 1){
                 shuffleSwitch();
                 playedSongs.clear();
                }
                return index;
            }
        }
    }

    // Atualiza o progreesso da barra
    public void updateSeekbar() {
        Thread timer = new Thread(() -> {
                int totalProgress = mediaPlayer.getDuration();
                int currentProgress = 0;

                while (currentProgress < totalProgress){
                    try {
                        if (currentProgress == 0){
                            Thread.sleep(5000);
                        }
                        Thread.sleep(500);
                        currentProgress = mediaPlayer.getCurrentPosition();
                        sbPlayerBar.setProgress(currentProgress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        });
        sbPlayerBar.setMax(mediaPlayer.getDuration());
        timer.start();
    }

    // Atualiza as informações e botões na tela do player
    private void setMusicPlayerUp() {
        tvSongTitle.setText(songList.get(currentSong).getTitle());
        updateSeekbar();

        if (!mediaPlayer.isPlaying()){
            ivPausePlay.setImageResource(R.drawable.ic_play_circle);
        } else {
            ivPausePlay.setImageResource(R.drawable.ic_pause_circle);
        }

        switch (currentState){
            case REPEAT_ON:
                ivRepeat.setImageResource(R.drawable.ic_repeat);
                break;
            case REPEAT_ONE_ON:
                ivRepeat.setImageResource(R.drawable.ic_repeat_one_on);
                mediaPlayer.setLooping(true);
                break;
            case SHUFFLE_ON:
                ivShuffle.setImageResource(R.drawable.ic_shuffle_on);
                break;
        }
    }
}