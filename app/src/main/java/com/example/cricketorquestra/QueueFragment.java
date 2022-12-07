package com.example.cricketorquestra;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class QueueFragment extends Fragment {
    static QueueAdapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvQueue;
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

        rvQueue = view.findViewById(R.id.rvQueue);
        rvQueue.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        rvQueue.setLayoutManager(layoutManager);

        myAdapter = new QueueAdapter(this.getContext(), MainActivity.songList);
        ItemTouchHelper.Callback callBack = new ItemTouchHelperClass(myAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callBack);
        touchHelper.attachToRecyclerView(rvQueue);
        rvQueue.setAdapter(myAdapter);
    }

    public static void refreshQueueList(ArrayList<SongClass> queueList) {
        myAdapter.queueList = queueList;
        MainActivity.queueList = queueList;
        myAdapter.notifyDataSetChanged();
    }

    private class ItemTouchHelperClass extends androidx.recyclerview.widget.ItemTouchHelper.Callback {
        private final ItemTouchHandler adapter;

        ItemTouchHelperClass (ItemTouchHandler adapter){
             this.adapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
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
            adapter.onSwipe(viewHolder.getAdapterPosition());
        }
    }
}