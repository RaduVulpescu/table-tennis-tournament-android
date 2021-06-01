package com.example.tabletennistournament.modules.cup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;

public class FixturePlayerListItemViewHolder extends RecyclerView.ViewHolder {

    public final TextView playerName;
    public final TextView playerQuality;

    public FixturePlayerListItemViewHolder(@NonNull View view) {
        super(view);
        this.playerName = itemView.findViewById(R.id.text_view_main_fixture_participant_name);
        this.playerQuality = itemView.findViewById(R.id.text_view_main_fixture_participant_quality);
    }

    @NonNull
    public static FixturePlayerListItemViewHolder create(@NonNull ViewGroup parent) {
        return new FixturePlayerListItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_fixture_participants_list_item, parent, false));
    }

}
