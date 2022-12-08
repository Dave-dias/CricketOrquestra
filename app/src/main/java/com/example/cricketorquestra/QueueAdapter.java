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

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> implements ItemTouchHandler{
    static MusicHandler musicHandler;

    ArrayList<SongClass> queueList;

    QueueAdapter (Context context, ArrayList<SongClass> queueList){
        musicHandler = (MusicHandler) context;
        this.queueList = queueList;
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
        holder.getTvCardName().setText((queueList.get(position).getTitle()));
    }

    @Override
    public int getItemCount() {
        return queueList.size();
    }

    @Override
    public void onDrag(int oldPosition, int newPosition) {
        Collections.swap(queueList, oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
        MainActivity.queueList = queueList;

        if (MainActivity.currentSong == oldPosition){
            MainActivity.currentSong = newPosition;
        }
    }

    @Override
    public void onSwipe(int position) {
        MainActivity.queueList.remove(position);
        queueList = MainActivity.queueList;
        notifyDataSetChanged();
    }
}
