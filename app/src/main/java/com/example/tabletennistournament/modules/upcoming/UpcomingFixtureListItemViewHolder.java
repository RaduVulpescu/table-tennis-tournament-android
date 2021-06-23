package com.example.tabletennistournament.modules.upcoming;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;
import com.google.android.material.card.MaterialCardView;

public class UpcomingFixtureListItemViewHolder extends RecyclerView.ViewHolder {

    public final MaterialCardView card;

    public final TextView fixtureDate;
    public final TextView fixtureTime;
    public final TextView fixtureLocation;
    public final TextView fixtureQualityAvg;
    public final TextView fixtureType;

    public final Button expandButton;
    public final Button collapseButton;

    public final LinearLayout linearLayoutUpcomingFixturePlayers;
    public final RecyclerView recyclerViewPlayers;

    public final View upcomingButtonsDelimiter;
    public final LinearLayout upcomingButtonsLinearLayout;
    public final Button startFixtureButton;
    public final Button editFixtureButton;
    public final Button deleteFixtureButton;

    public UpcomingFixtureListItemViewHolder(@NonNull View view) {
        super(view);
        this.card = itemView.findViewById(R.id.material_card_view_upcoming_fixture);

        this.fixtureDate = itemView.findViewById(R.id.text_view_upcoming_date);
        this.fixtureTime = itemView.findViewById(R.id.text_view_upcoming_time);
        this.fixtureLocation = itemView.findViewById(R.id.text_view_upcoming_location);
        this.fixtureQualityAvg = itemView.findViewById(R.id.text_view_upcoming_qavg);
        this.fixtureType = itemView.findViewById(R.id.text_view_fixture_type);

        this.expandButton = itemView.findViewById(R.id.button_upcoming_expand);
        this.collapseButton = itemView.findViewById(R.id.button_upcoming_collapse);

        this.linearLayoutUpcomingFixturePlayers = itemView.findViewById(R.id.linear_layout_upcoming_fixture_players);
        this.recyclerViewPlayers = itemView.findViewById(R.id.recycler_view_upcoming_fixture_players);

        this.upcomingButtonsDelimiter = itemView.findViewById(R.id.delimiter_upcoming_buttons);
        this.upcomingButtonsLinearLayout = itemView.findViewById(R.id.linear_layout_upcoming_buttons);
        this.startFixtureButton = itemView.findViewById(R.id.button_upcoming_start);
        this.editFixtureButton = itemView.findViewById(R.id.button_upcoming_edit);
        this.deleteFixtureButton = itemView.findViewById(R.id.button_upcoming_delete);
    }

    @NonNull
    public static UpcomingFixtureListItemViewHolder create(@NonNull ViewGroup parent) {
        return new UpcomingFixtureListItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_fixture_list_item, parent, false));
    }

    public void setVisibilityOnGone() {
        this.card.setVisibility(View.GONE);
    }

}
