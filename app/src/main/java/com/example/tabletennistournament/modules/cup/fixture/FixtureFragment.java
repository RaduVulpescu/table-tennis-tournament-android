package com.example.tabletennistournament.modules.cup.fixture;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;
import com.example.tabletennistournament.enums.Group;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.models.FixturePlayer;
import com.example.tabletennistournament.models.GroupMatch;
import com.example.tabletennistournament.services.GsonSingleton;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FixtureFragment extends Fragment {

    private static final String ARG_FIXTURE_JSON = "FIXTURE_JSON";

    boolean informationIsExpanded = true;
    boolean groupsIsExpended = false;
    boolean pyramidsIsExpended = false;
    boolean rankingIsExpended = false;

    Gson gson;
    RequestQueueSingleton requestQueue;
    FixtureModel fixture;
    View fragmentView;

    LinearLayout information_linear_fixture_content_container;

    ImageButton information_expand_button;
    ImageButton groups_expand_button;
    ImageButton pyramids_expand_button;
    ImageButton ranking_expand_button;
    LinearLayout information_linear_layout;
    LinearLayout groups_linear_layout;
    LinearLayout pyramids_linear_layout;
    LinearLayout ranking_linear_layout;

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

        information_linear_fixture_content_container = fragmentView.findViewById(R.id.linear_layout_fixture_content_container);

        information_expand_button = fragmentView.findViewById(R.id.button_fixture_information_expand_collapse);
        groups_expand_button = fragmentView.findViewById(R.id.button_fixture_groups_expand_collapse);
        pyramids_expand_button = fragmentView.findViewById(R.id.button_fixture_pyramids_expand_collapse);
        ranking_expand_button = fragmentView.findViewById(R.id.button_fixture_ranking_expand_collapse);
        information_linear_layout = fragmentView.findViewById(R.id.linear_layout_information_container);
        groups_linear_layout = fragmentView.findViewById(R.id.linear_layout_groups_container);
        pyramids_linear_layout = null;
        ranking_linear_layout = null;

        setOnClickToExpandButtons();
        populateFixtureData(fixture);
        populateParticipantsList(fixture.Players);
        populateChipGroup(fixture.GroupMatches);
        //populateGroups(fixture.GroupMatches);
    }

    private void setOnClickToExpandButtons() {

        Transition transition = new Slide(Gravity.TOP);
        TransitionManager.beginDelayedTransition(information_linear_fixture_content_container, transition);

        information_expand_button.setOnClickListener(v -> {
            if (informationIsExpanded) {
                information_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                information_linear_layout.setVisibility(View.GONE);
            } else {
                information_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                information_linear_layout.setVisibility(View.VISIBLE);
            }

            informationIsExpanded = !informationIsExpanded;
        });

        TransitionManager.beginDelayedTransition(information_linear_fixture_content_container, new AutoTransition());
        groups_expand_button.setOnClickListener(v -> {
            if (groupsIsExpended) {
                groups_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                groups_linear_layout.setVisibility(View.GONE);
            } else {
                groups_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                groups_linear_layout.setVisibility(View.VISIBLE);
            }

            groupsIsExpended = !groupsIsExpended;
        });

        pyramids_expand_button.setOnClickListener(v -> {
            if (pyramidsIsExpended) {
                pyramids_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                //pyramids_linear_layout.setVisibility(View.GONE);
            } else {
                pyramids_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                //pyramids_linear_layout.setVisibility(View.VISIBLE);
            }

            pyramidsIsExpended = !pyramidsIsExpended;
        });

        ranking_expand_button.setOnClickListener(v -> {
            if (rankingIsExpended) {
                ranking_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                //ranking_linear_layout.setVisibility(View.GONE);
            } else {
                ranking_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                //ranking_linear_layout.setVisibility(View.VISIBLE);
            }

            rankingIsExpended = !rankingIsExpended;
        });
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
        recyclerView.setLayoutManager(new LinearLayoutManager(fragmentView.getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

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

    private void populateChipGroup(@NonNull List<GroupMatch> groupMatches) {
        ChipGroup chipGroup = fragmentView.findViewById(R.id.chip_group_groups_list);

        List<Group> groups = groupMatches.stream().map(x -> x.Group).distinct().collect(Collectors.toList());

        for (int i = 0, groupsSize = groups.size(); i < groupsSize; i++) {
            Group group = groups.get(i);
            Chip chip = new Chip(this.getActivity());
            ChipDrawable drawable = ChipDrawable.createFromAttributes(this.getActivity(), null,
                    0, R.style.Widget_MaterialComponents_Chip_Choice);
            chip.setChipDrawable(drawable);
            chip.setText(String.format("Group %s", group));

            List<GroupMatch> groupMatchesForGroup = groupMatches.stream().filter(x -> x.Group == group).collect(Collectors.toList());

            chip.setOnClickListener(v -> {
                chipGroup.clearCheck();
                ((Chip) v).setChecked(true);

                populateGroups(groupMatchesForGroup);
            });

            if (i == 0) {
                chip.setChecked(true);
                populateGroups(groupMatchesForGroup);
            }

            chipGroup.addView(chip);
        }
    }

    private void populateGroups(List<GroupMatch> groupMatches) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view_group, GroupFragment.newInstance(gson.toJson(groupMatches)))
                .commit();
    }
}
