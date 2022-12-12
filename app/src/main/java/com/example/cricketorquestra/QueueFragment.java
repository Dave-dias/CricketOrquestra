package com.example.cricketorquestra;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueFragment extends Fragment implements QueueHandler {
    MaterialToolbar topToolbar;
    ImageButton ivRefreshList;
    RecyclerView rvQueue;

    QueueAdapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_queue, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        topToolbar = view.findViewById(R.id.topToolbar);
        ivRefreshList = view.findViewById(R.id.ivRefreshList);

        ivRefreshList.setOnClickListener(v -> refreshQueue(SongCase.songList));

        rvQueue = view.findViewById(R.id.rvQueue);
        rvQueue.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        rvQueue.setLayoutManager(layoutManager);

        myAdapter = new QueueAdapter(this.getContext());
        ItemTouchHelper.Callback callBack = new ItemTouchHelperClass(myAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callBack);
        touchHelper.attachToRecyclerView(rvQueue);
        rvQueue.setAdapter(myAdapter);
    }

    @Override
    public void setViewsUp() {
        topToolbar.setSubtitle(CurrentMusic.getThisObject().getTitle());
    }

    @Override
    public void refreshQueue(ArrayList<SongClass> newQueueList) {
        SongCase.queueList = newQueueList;
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void sortQueue() {
        Executor backgroundThread = Executors.newSingleThreadExecutor();
        SongCase.sortedList = new ArrayList<>();

        backgroundThread.execute(() -> {
            while (SongCase.sortedList.size() <= SongCase.songList.size()) {
                // Gera um numero aleatorio dentro do intervado entre 0 e o tamanho do array de musicas
                int index = (int) Math.floor((Math.random() * SongCase.songList.size()));
                if (!SongCase.sortedList.contains(SongCase.songList.get(index))) {
                    SongCase.sortedList.add(SongCase.songList.get(index));
                }
            }
        });
    }

    private class ItemTouchHelperClass extends androidx.recyclerview.widget.ItemTouchHelper.Callback {
        private final ItemTouchHandler adapter;

        ItemTouchHelperClass (ItemTouchHandler adapter){
             this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            final int dragFlags = androidx.recyclerview.widget.ItemTouchHelper.UP |
                    androidx.recyclerview.widget.ItemTouchHelper.DOWN;
            final int swipeFlags = androidx.recyclerview.widget.ItemTouchHelper.LEFT |
                    androidx.recyclerview.widget.ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            adapter.onDrag(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder.getAdapterPosition() == CurrentMusic.getIndex()){
                refreshQueue(SongCase.queueList);
            } else {
                adapter.onSwipe(viewHolder.getAdapterPosition());
            }
        }
    }
}