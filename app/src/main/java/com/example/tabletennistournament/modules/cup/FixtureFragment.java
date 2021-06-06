package com.example.tabletennistournament.modules.cup;

import android.os.Bundle;
import android.view.LayoutInflater;
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

    private static final String ARG_FIXTURE_JSON = "FIXTURE_JSON";

    Gson gson;
    RequestQueueSingleton requestQueue;
    FixtureModel fixture;
    View fragmentView;

    public FixtureFragment() {
    }

    @NonNull
    public static FixtureFragment newInstance(String fixtureJson) {
        FixtureFragment fragment = new FixtureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FIXTURE_JSON, fixtureJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null) return;

        gson = GsonSingleton.getInstance();
        requestQueue = RequestQueueSingleton.getInstance(getActivity().getBaseContext());

        String fixtureJson = getArguments().getString(ARG_FIXTURE_JSON);
        fixture = gson.fromJson(fixtureJson, FixtureModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_fixture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentView = view;

        populateFixtureData(fixture);
        populateParticipantsList(fixture.Players);
    }

    private void populateFixtureData(@NonNull FixtureModel fixture) {
        TextView locationTextView = fragmentView.findViewById(R.id.text_view_main_fixture_location_placeholder);
        TextView dateTextView = fragmentView.findViewById(R.id.text_view_main_fixture_date_placeholder);
        TextView qualityAverageTextView = fragmentView.findViewById(R.id.text_view_main_fixture_quality_average_placeholder);
        TextView stateTextView = fragmentView.findViewById(R.id.text_view_main_fixture_state_placeholder);

        locationTextView.setText(String.format("Location: %s", fixture.Location));
        dateTextView.setText(String.format("Date: %s", fixture.Date.format(DateTimeFormatter.ofPattern("dd MMMM HH:mm"))));
        qualityAverageTextView.setText(String.format(Locale.getDefault(), "Quality Avg: %.2f", fixture.QualityAverage));
        stateTextView.setText(String.format("State: %s", fixture.State));
    }

    private void populateParticipantsList(List<FixturePlayer> players) {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_main_participants);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragmentView.getContext()));

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
