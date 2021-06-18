package com.example.tabletennistournament.modules.cup.fixture.deciders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;

public class PyramidMatchesItemViewHolder extends RecyclerView.ViewHolder {

    public final TextView playerOneName;
    public final TextView matchScore;
    public final TextView playerTwoName;

    public PyramidMatchesItemViewHolder(@NonNull View view) {
        super(view);
        this.playerOneName = itemView.findViewById(R.id.text_view_decider_match_player_one_name);
        this.matchScore = itemView.findViewById(R.id.text_view_decider_match_score);
        this.playerTwoName = itemView.findViewById(R.id.text_view_decider_match_player_two_name);
    }

    @NonNull
    public static PyramidMatchesItemViewHolder create(@NonNull ViewGroup parent) {
        return new PyramidMatchesItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pyramid_matches_list_item, parent, false));
    }

}
