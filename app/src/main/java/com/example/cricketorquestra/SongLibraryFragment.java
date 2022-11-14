package com.example.cricketorquestra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SongLibraryFragment extends Fragment {
    static MusicHandler musicHandler;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    SongLibraryAdapter myAdapter;
    View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_song_library, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        musicHandler = (MusicHandler) this.getContext();

        recyclerView = view.findViewById(R.id.rvSongList);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        myAdapter = new SongLibraryAdapter(musicHandler);
        recyclerView.setAdapter(myAdapter);
    }

}