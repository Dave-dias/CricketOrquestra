package com.example.cricketorquestra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongLibraryAdapter extends RecyclerView.Adapter<SongLibraryAdapter.ViewHolder> {
    static MusicHandler musicHandler;

    SongLibraryAdapter (Context context){
        musicHandler = (MusicHandler) context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvCardName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardName = itemView.findViewById(R.id.tvPlayingTitle);

            itemView.setOnClickListener(v -> musicHandler.onSelectedMusicLibrary(getAdapterPosition()));
        }

        public TextView getTvCardName() {
            return tvCardName;
        }
    }

    @NonNull
    @Override
    public SongLibraryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongLibraryAdapter.ViewHolder holder, int position) {
        holder.getTvCardName().setText((Application.songList.get(position).getTitle()));
    }

    @Override
    public int getItemCount() {
        return Application.songList.size();
    }

}
