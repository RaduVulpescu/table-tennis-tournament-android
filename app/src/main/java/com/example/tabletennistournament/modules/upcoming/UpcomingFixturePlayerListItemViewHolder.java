package com.example.tabletennistournament.modules.upcoming;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;

public class UpcomingFixturePlayerListItemViewHolder extends RecyclerView.ViewHolder {

    public final TextView playerName;
    public final TextView playerQuality;

    public UpcomingFixturePlayerListItemViewHolder(@NonNull View view) {
        super(view);
        this.playerName = itemView.findViewById(R.id.text_view_upcoming_fixture_player_name);
        this.playerQuality = itemView.findViewById(R.id.text_view_upcoming_fixture_player_quality);
    }

    @NonNull
    public static UpcomingFixturePlayerListItemViewHolder create(@NonNull ViewGroup parent) {
        return new UpcomingFixturePlayerListItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_fixture_players_list_item, parent, false));
    }

}
