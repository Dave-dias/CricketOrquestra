package com.example.cricketorquestra;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements MusicHandler {

    enum displayedFragment {SONG_LIBRARY, MUSIC_PLAYER, QUEUE_LIST}

    ImageView ivCover, ivPausePlay, ivSkipNext, ivSkipPrevious, ivShuffle, ivRepeat;
    TextView tvSongTitle, tvNavBarLibrary, tvNavBarPlayer, tvNavBarQueue;
    ProgressBar pbSongList;
    SeekBar sbPlayerBar;
    MaterialToolbar topToolbar;

    Fragment MusicPlayerFrag, SongLibraryFrag, QueueFrag;
    FragmentManager fragmentManager;
    MediaPlayer mediaPlayer;

    static ArrayList<SongClass> songList;
    static ArrayList<SongClass> queueList;
    static ArrayList<SongClass> sortedList;
    static int currentSong = 0;
    Drawable drwPlayer, drwLibrary;
    PlayerStates currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_main);

        // Costumizando a actionbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher_foreground);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        // Setando os fragementos, arrays e criando o media player
        currentState = PlayerStates.REPEAT_ON;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(false);
        setReceiverUp();
        loadSongList();

        fragmentManager = getSupportFragmentManager();
        SongLibraryFrag = new SongLibraryFragment();
        MusicPlayerFrag = new MusicPlayerFragment();
        QueueFrag = new QueueFragment();

        fragmentManager.beginTransaction()
                .add(R.id.FragmentContainer, SongLibraryFrag)
                .add(R.id.FragmentContainer, MusicPlayerFrag)
                .add(R.id.FragmentContainer, QueueFrag)
                .hide(SongLibraryFrag)
                .hide(QueueFrag)
                .show(MusicPlayerFrag)
                .commitNow();
    }

    // Caso tente setar no onCreate, as views não são encontradas
    // pois não foram criadas ainda
    @Override
    protected void onResume() {
        super.onResume();
        setOnViews();
        drwLibrary = tvNavBarLibrary.getCompoundDrawablesRelative()[3];
        drwPlayer = tvNavBarPlayer.getCompoundDrawablesRelative()[3];
        drwLibrary.setBounds(tvNavBarLibrary.getCompoundDrawablesRelative()[3].getBounds());
        drwPlayer.setBounds(tvNavBarPlayer.getCompoundDrawablesRelative()[3].getBounds());
    }

    @Override
    protected void onDestroy() {
        NotificationManager manager = this.getSystemService(NotificationManager.class);
        manager.cancel(0);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getSupportActionBar() != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                tvNavBarPlayer.setCompoundDrawables(null, null, null, null);
                tvNavBarLibrary.setCompoundDrawables(null, null, null, null);
                ivCover.setVisibility(View.GONE);
                getSupportActionBar().hide();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                tvNavBarPlayer.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null, null, null, drwPlayer);
                tvNavBarLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null, null, null, drwLibrary);
                ivCover.setVisibility(View.VISIBLE);
                getSupportActionBar().show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        fragmentSwitch(displayedFragment.MUSIC_PLAYER);
    }

    // Classe que recebe o broadcast do NotificationReceiver
    public BroadcastReceiver MainActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case SplashScreenActivity.ACTION_PREVIOUS:
                        previousAudio();
                        break;

                    case SplashScreenActivity.ACTION_PLAY_PAUSE:
                        playPauseSwitch();
                        break;

                    case SplashScreenActivity.ACTION_NEXT:
                        nextAudio();
                        break;
                }
            }
        }
    };

    // Registra o receiver local
    private void setReceiverUp() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("Play/Pause");
        filter.addAction("Previous");
        filter.addAction("Next");
        registerReceiver(MainActivityReceiver, filter);
    }

    private void setOnViews() {
        tvNavBarLibrary = findViewById(R.id.tvNavLibrary);
        tvNavBarQueue = findViewById(R.id.tvNavQueue);
        tvNavBarPlayer = findViewById(R.id.tvNavPlayer);
        ivCover = findViewById(R.id.ivCover);
        ivPausePlay = findViewById(R.id.ivPausePlay);
        ivSkipNext = findViewById(R.id.ivSkipNext);
        ivSkipPrevious = findViewById(R.id.ivSkipPrevious);
        ivShuffle = findViewById(R.id.ivShuffle);
        ivRepeat = findViewById(R.id.ivRepeat);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        sbPlayerBar = findViewById(R.id.sbPlayerBar);
        pbSongList = findViewById(R.id.pbSongList);
        topToolbar = findViewById(R.id.topToolbar);

        setOnListeners();
    }

    private void setOnListeners() {
        tvNavBarPlayer.setOnClickListener(v -> fragmentSwitch(displayedFragment.MUSIC_PLAYER));

        tvNavBarLibrary.setOnClickListener(v -> fragmentSwitch(displayedFragment.SONG_LIBRARY));

        tvNavBarQueue.setOnClickListener(v -> fragmentSwitch(displayedFragment.QUEUE_LIST));

        ivPausePlay.setOnClickListener(v -> playPauseSwitch());

        ivSkipNext.setOnClickListener(v -> nextAudio());

        ivSkipPrevious.setOnClickListener(v -> previousAudio());

        ivShuffle.setOnClickListener(v -> shuffleSwitch());

        ivRepeat.setOnClickListener(v -> repeatSwitch());

        mediaPlayer.setOnCompletionListener(mp -> nextAudio());

        mediaPlayer.setOnErrorListener((mp, what, extra) -> true);

        mediaPlayer.setOnPreparedListener(mp -> {
            // Quando o player estiver pronto este listener dá play no audio
            // e seta as informações dela
            mp.start();
            setAudioUp();
        });

        sbPlayerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                    }
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

    void fragmentSwitch(displayedFragment display) {
        switch (display) {
            case SONG_LIBRARY:
                if (getSupportActionBar() != null){
                    getSupportActionBar().show();
                }
                fragmentManager.beginTransaction()
                        .show(SongLibraryFrag)
                        .hide(MusicPlayerFrag)
                        .hide(QueueFrag)
                        .commitNow();
                break;
            case MUSIC_PLAYER:
                if (getSupportActionBar() != null){
                    getSupportActionBar().show();
                }
                fragmentManager.beginTransaction()
                        .show(MusicPlayerFrag)
                        .hide(SongLibraryFrag)
                        .hide(QueueFrag)
                        .commitNow();
                break;

            case QUEUE_LIST:
                if (getSupportActionBar() != null){
                    getSupportActionBar().hide();
                }
                fragmentManager.beginTransaction()
                        .show(QueueFrag)
                        .hide(MusicPlayerFrag)
                        .hide(SongLibraryFrag)
                        .commitNow();
                break;
        }
    }

    // Cria notificação, seta ações e dá display nela
    private void showNotification() {
        Intent contentIntent = new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this,
                0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        Intent previousIntent = new Intent(this, NotificationReceiver.class)
                .setAction(SplashScreenActivity.ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this,
                0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        Intent playPauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(SplashScreenActivity.ACTION_PLAY_PAUSE);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this,
                0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(SplashScreenActivity.ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,
                0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, SplashScreenActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle("Playing now...")
                .setContentText(queueList.get(currentSong).getTitle())
                .setContentIntent(contentPendingIntent)
                .addAction(R.drawable.ic_skip_previous, "Previous", previousPendingIntent)
                .addAction(R.drawable.ic_play_circle, "Play/Pause", playPausePendingIntent)
                .addAction(R.drawable.ic_skip_next, "Next", nextPendingIntent)
                .setOnlyAlertOnce(true);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(0, builder.build());
    }

    // Reseta o media player e dá start na musica selecionada
    @Override
    public void onSelectedMusicLibrary(int index) {
        currentSong = index;
        clearMediaPlayer();
        try {
            mediaPlayer.setDataSource(songList.get(index).getSourceFolder());
            mediaPlayer.prepareAsync();
            resetQueue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSelectedQueue(int index) {
        currentSong = index;
        clearMediaPlayer();
        try {
            mediaPlayer.setDataSource(queueList.get(index).getSourceFolder());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reseta o Media player
    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    public void playPauseSwitch() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
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
            resetQueue();
        } else {
            currentState = PlayerStates.SHUFFLE_ON;
            ivShuffle.setImageResource(R.drawable.ic_shuffle_on);
            ivRepeat.setImageResource(R.drawable.ic_repeat);
            setSortedList();
        }
        mediaPlayer.setLooping(false);
    }

    // Alterna os estados do player
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
            resetQueue();
        }
    }

    // Verifica se existe audio anterior, se existe seleciona o audio
    public void previousAudio() {
        // Caso o shuffle esteja ativado chama um novo audio aleatorio
        if (currentSong == 0) {
            onSelectedQueue(currentSong);
        } else {
            onSelectedQueue(currentSong - 1);
        }
    }

    // Verifica se existe audio posterior, se existe seleciona o audio
    public void nextAudio() {
        // Caso o shuffle esteja ativado chama um o metodo de sorteio
        if (currentSong == queueList.size() - 1) {
            onSelectedQueue(currentSong);
            resetQueue();
        } else {
            onSelectedQueue(currentSong + 1);
        }
    }

    private void sortQueue() {
        Executor backgroundThread = Executors.newSingleThreadExecutor();
        sortedList = new ArrayList<>();

        backgroundThread.execute(() -> {
            while (sortedList.size() <= songList.size()) {
                // Gera um numero aleatorio dentro do intervado entre 0 e o tamanho do array de musicas
                int index = (int) Math.floor((Math.random() * songList.size()));
                if (!sortedList.contains(songList.get(index))) {
                    sortedList.add(songList.get(index));
                }
            }
        });
    }

    private void resetQueue() {
        QueueFragment.refreshQueueList(songList);
        sortQueue();
    }

    private void setSortedList() {
        QueueFragment.refreshQueueList(sortedList);
    }

    // Atualiza o progreesso da barra
    private void updateSeekbar() {
        Thread timer = new Thread(() -> {
            int totalProgress = mediaPlayer.getDuration();
            int currentProgress = 0;

            while (currentProgress < totalProgress) {
                try {
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
    private void setAudioUp() {
        showNotification();
        updateSeekbar();

        topToolbar.setTitle(queueList.get(currentSong).getTitle());
        tvSongTitle.setText(queueList.get(currentSong).getTitle());

        if (!mediaPlayer.isPlaying()) {
            ivPausePlay.setImageResource(R.drawable.ic_play_circle);
        } else {
            ivPausePlay.setImageResource(R.drawable.ic_pause_circle);
        }

        switch (currentState) {
            case REPEAT_ON:
                ivRepeat.setImageResource(R.drawable.ic_repeat);
                break;
            case REPEAT_ONE_ON:
                ivRepeat.setImageResource(R.drawable.ic_repeat_one_on);
                break;
            case SHUFFLE_ON:
                ivShuffle.setImageResource(R.drawable.ic_shuffle_on);
                break;
        }
    }

    // Chama a função de scannear os arquivos e adiciona eles ao array
    public void loadSongList() {
        Executor backgroundThread = Executors.newSingleThreadExecutor();
        Handler postExecute = new Handler(Looper.getMainLooper());
        songList = new ArrayList<>();

        backgroundThread.execute(() -> {
            ArrayList<File> fileArray = findFiles(Environment.getExternalStorageDirectory());

            for (File singleFile : fileArray) {
                songList.add(new SongClass(singleFile.getName().replace(".mp3", "").replace(".wav", "")
                        , singleFile.getPath()));
            }

            postExecute.post(() -> {
                pbSongList.setVisibility(View.INVISIBLE);
                if (songList.size() != 0) {
                    //Seleciona a primeira musica da lista e atualiza o recycleview
                    SongLibraryFragment.refreshRecycleview(songList);
                    onSelectedMusicLibrary(0);
                    sortQueue();
                } else {
                    Toast.makeText(MainActivity.this, "No audio file was found", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        });
    }

    public ArrayList<File> findFiles(File fileToScan) {
        ArrayList<File> fileArray = new ArrayList<>();
        File[] files = fileToScan.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                // Caso o arquivo seja um diretorio chama ela propria passando o arquivo
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArray.addAll(findFiles(singleFile));
                } else if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    fileArray.add(singleFile);
                }
            }
        }
        return fileArray;
    }
}