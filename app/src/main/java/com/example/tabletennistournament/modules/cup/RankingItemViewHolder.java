package com.example.tabletennistournament.modules.cup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;
import com.google.android.material.card.MaterialCardView;

public class RankingItemViewHolder extends RecyclerView.ViewHolder {

    public final MaterialCardView cardView;

    public final TextView playerRank;
    public final TextView playerName;
    public final ImageView playerLevelIcon;
    public final TextView playerQuality;
    public final TextView playerTop4;

    public final TextView playerScore1;
    public final TextView playerScore2;
    public final TextView playerScore3;
    public final TextView playerScore4;
    public final TextView playerShape;

    public final LinearLayout linearLayoutExtraStats;
    public final ImageButton expandButton;

    public boolean isExpanded = false;

    public RankingItemViewHolder(@NonNull View view) {
        super(view);
        this.cardView = itemView.findViewById(R.id.card);

        this.playerRank = itemView.findViewById(R.id.text_view_ranking_rank);
        this.playerName = itemView.findViewById(R.id.text_view_ranking_name);
        this.playerLevelIcon = itemView.findViewById(R.id.text_view_ranking_level);
        this.playerQuality = itemView.findViewById(R.id.text_view_ranking_quality);
        this.playerTop4 = itemView.findViewById(R.id.text_view_ranking_top4);

        this.playerScore1 = itemView.findViewById(R.id.text_view_ranking_score1);
        this.playerScore2 = itemView.findViewById(R.id.text_view_ranking_score2);
        this.playerScore3 = itemView.findViewById(R.id.text_view_ranking_score3);
        this.playerScore4 = itemView.findViewById(R.id.text_view_ranking_score4);
        this.playerShape = itemView.findViewById(R.id.text_view_ranking_shape);

        this.linearLayoutExtraStats = itemView.findViewById(R.id.linear_layout_ranking_extra_stats);
        this.expandButton = itemView.findViewById(R.id.button_ranking_expand_collapse);
    }

    @NonNull
    public static RankingItemViewHolder create(@NonNull ViewGroup parent) {
        return new RankingItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ranking_list_item, parent, false));
    }

}
