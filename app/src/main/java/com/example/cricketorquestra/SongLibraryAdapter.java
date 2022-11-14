package com.example.cricketorquestra;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongLibraryAdapter extends RecyclerView.Adapter<SongLibraryAdapter.ViewHolder> {
    static MusicHandler musicHandler;

    ArrayList<SongClass> songList;

    SongLibraryAdapter (MusicHandler fromParent, ArrayList<SongClass> songList){
        musicHandler = fromParent;
        this.songList = songList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvCardName;
        private final ImageView ivCardCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCardCover = (ImageView) itemView.findViewById(R.id.ivCardCover);
            tvCardName = itemView.findViewById(R.id.tvCardName);

            itemView.setOnClickListener(v -> {
                musicHandler.onMusicSelected(getAdapterPosition());
            });
        }

        public TextView getTvCardName() {
            return tvCardName;
        }

        public ImageView getIvCardCover() {
            return ivCardCover;
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
        holder.getIvCardCover();
        holder.getTvCardName().setText((songList.get(position).getTitle().toString()));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
