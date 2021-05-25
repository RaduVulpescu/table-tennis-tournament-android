package com.example.tabletennistournament.modules.players;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;

public class PlayerListItemViewHolder extends RecyclerView.ViewHolder {

    public final ImageView profilePicture;
    public final TextView playerName;
    public final ImageView playerLevelIcon;
    public final TextView playerQuality;

    public PlayerListItemViewHolder(@NonNull View view) {
        super(view);
        this.profilePicture = itemView.findViewById(R.id.player_list_item_avatar);
        this.playerName = itemView.findViewById(R.id.player_list_item_name);
        this.playerLevelIcon = itemView.findViewById(R.id.player_list_item_level_icon);
        this.playerQuality = itemView.findViewById(R.id.player_list_item_quality);
    }

    @NonNull
    public static PlayerListItemViewHolder create(@NonNull ViewGroup parent) {
        return new PlayerListItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_list_item, parent, false));
    }

}
