package com.example.cricketorquestra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> implements ItemTouchHandler{
    static MusicHandler musicHandler;

    QueueAdapter (Context context){
        musicHandler = (MusicHandler) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvCardName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardName = itemView.findViewById(R.id.tvPlayingTitle);

            itemView.setOnClickListener(v -> musicHandler.onSelectedQueue(getAdapterPosition()));
        }

        public TextView getTvCardName() {
            return tvCardName;
        }
    }

    @NonNull
    @Override
    public QueueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueueAdapter.ViewHolder holder, int position) {
        holder.getTvCardName().setText((Application.queueList.get(position).getTitle()));
    }

    @Override
    public int getItemCount() {
        return Application.queueList.size();
    }

    @Override
    public void onDrag(int oldPosition, int newPosition) {
        Collections.swap(Application.queueList, oldPosition, newPosition);
        this.notifyItemMoved(oldPosition, newPosition);

        if (CurrentMusic.getIndex() == oldPosition){
            CurrentMusic.setIndex(newPosition);
        }
    }

    @Override
    public void onSwipe(int position) {
        Application.queueList.remove(position);
        this.notifyItemRemoved(position);
    }
}
