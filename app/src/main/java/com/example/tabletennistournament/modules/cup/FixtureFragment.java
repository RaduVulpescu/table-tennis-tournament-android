package com.example.tabletennistournament.modules.cup;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.models.FixturePlayer;
import com.example.tabletennistournament.services.GsonSingleton;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.gson.Gson;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FixtureFragment extends Fragment {

    Gson gson;
    RequestQueueSingleton requestQueue;
    FixtureModel fixture;
    View view;

    public static final String FIXTURE_JSON = "FIXTURE_JSON";

    public FixtureFragment() {
        super(R.layout.fragment_main_fixture);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle argumentsBundle = getArguments();
        if (argumentsBundle == null) return;

        gson = GsonSingleton.getInstance();
        requestQueue = RequestQueueSingleton.getInstance(getActivity().getBaseContext());

        String fixtureJson = argumentsBundle.getString(FIXTURE_JSON);
        fixture = gson.fromJson(fixtureJson, FixtureModel.class);
        this.view = view;

        populateFixtureData(fixture);
        populateParticipantsList(fixture.Players);
    }

    private void populateFixtureData(@NonNull FixtureModel fixture) {
        TextView locationTextView = view.findViewById(R.id.text_view_main_fixture_location_placeholder);
        TextView dateTextView = view.findViewById(R.id.text_view_main_fixture_date_placeholder);
        TextView qualityAverageTextView = view.findViewById(R.id.text_view_main_fixture_quality_average_placeholder);
        TextView stateTextView = view.findViewById(R.id.text_view_main_fixture_state_placeholder);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm");

        locationTextView.setText(String.format("Location: %s", fixture.Location));
        dateTextView.setText(String.format("Date: %s", fixture.Date.format(formatter)));
        qualityAverageTextView.setText(String.format(Locale.getDefault(), "Quality Avg: %.2f", fixture.QualityAverage));
        stateTextView.setText(String.format("State: %s", fixture.State));
    }

    private void populateParticipantsList(List<FixturePlayer> players) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_main_participants);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        RecyclerView.Adapter<RecyclerView.ViewHolder> participantsListAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return FixturePlayerListItemViewHolder.create(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                bind((FixturePlayerListItemViewHolder) viewHolder, position);
            }

            @Override
            public int getItemCount() {
                return players.size();
            }

            private void bind(@NonNull FixturePlayerListItemViewHolder vh, int position) {
                FixturePlayer player = players.get(position);

                vh.playerName.setText(player.Name);
                vh.playerQuality.setText(String.format(Locale.getDefault(), "Q: %.2f", player.Quality));
            }
        };

        recyclerView.setAdapter(participantsListAdapter);
    }

}
