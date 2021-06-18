package com.example.tabletennistournament.modules.cup.fixture.ranking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;

public class FixtureRankingItemViewHolder extends RecyclerView.ViewHolder {

    public final TextView playerRank;
    public final TextView playerName;
    public final TextView playerScore;

    public FixtureRankingItemViewHolder(@NonNull View view) {
        super(view);
        this.playerRank = itemView.findViewById(R.id.text_view_fixture_ranking_place);
        this.playerName = itemView.findViewById(R.id.text_view_fixture_ranking_player_name);
        this.playerScore = itemView.findViewById(R.id.text_view_fixture_ranking_score);
    }

    @NonNull
    public static FixtureRankingItemViewHolder create(@NonNull ViewGroup parent) {
        return new FixtureRankingItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fixture_ranking_list_item, parent, false));
    }

}
