package com.example.tabletennistournament.modules.cup.fixture;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.dto.MatchPutDTO;
import com.example.tabletennistournament.enums.FixtureState;
import com.example.tabletennistournament.enums.Group;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.models.FixturePlayer;
import com.example.tabletennistournament.models.GroupMatch;
import com.example.tabletennistournament.models.MatchModel;
import com.example.tabletennistournament.models.PlayerRank;
import com.example.tabletennistournament.models.Pyramid;
import com.example.tabletennistournament.modules.cup.fixture.deciders.PyramidItemViewHolder;
import com.example.tabletennistournament.modules.cup.fixture.deciders.PyramidMatchesItemViewHolder;
import com.example.tabletennistournament.modules.cup.fixture.group.GroupFragment;
import com.example.tabletennistournament.modules.cup.fixture.group.viewModels.FixtureViewModel;
import com.example.tabletennistournament.modules.cup.fixture.ranking.FixtureRankingItemViewHolder;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.GsonSingleton;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.example.tabletennistournament.services.Util;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class FixtureFragment extends Fragment {

    private static final String ARG_FIXTURE_JSON = "FIXTURE_JSON";

    boolean informationIsExpanded = false;
    boolean groupsIsExpanded = false;
    boolean pyramidsIsExpanded = false;
    boolean rankingIsExpanded = false;

    FixtureViewModel fixtureViewModel;

    Gson gson;
    RequestQueueSingleton requestQueue;
    FixtureModel fixture;
    View fragmentView;
    FragmentManager fragmentManager;

    LinearLayout information_linear_fixture_content_container;
    LinearLayout pyramids_linear_fixture_content_container;
    LinearLayout ranking_linear_fixture_content_container;

    ImageButton information_expand_button;
    ImageButton groups_expand_button;
    ImageButton pyramids_expand_button;
    ImageButton ranking_expand_button;
    LinearLayout information_linear_layout;
    LinearLayout groups_linear_layout;
    LinearLayout pyramids_linear_layout;
    LinearLayout ranking_linear_layout;

    TextView stateTextView;
    LinearProgressIndicator linearProgressIndicator;
    Button endGroupButton;

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

        fragmentManager = this.getChildFragmentManager();
        information_linear_fixture_content_container = fragmentView.findViewById(R.id.linear_layout_fixture_content_container);
        pyramids_linear_fixture_content_container = fragmentView.findViewById(R.id.linear_layout_pyramids_all_section);
        ranking_linear_fixture_content_container = fragmentView.findViewById(R.id.linear_layout_ranking_all_section);

        information_expand_button = fragmentView.findViewById(R.id.button_fixture_information_expand_collapse);
        groups_expand_button = fragmentView.findViewById(R.id.button_fixture_groups_expand_collapse);
        pyramids_expand_button = fragmentView.findViewById(R.id.button_fixture_pyramids_expand_collapse);
        ranking_expand_button = fragmentView.findViewById(R.id.button_fixture_ranking_expand_collapse);
        information_linear_layout = fragmentView.findViewById(R.id.linear_layout_information_container);
        groups_linear_layout = fragmentView.findViewById(R.id.linear_layout_groups_container);
        pyramids_linear_layout = fragmentView.findViewById(R.id.linear_layout_pyramids_container);
        ranking_linear_layout = fragmentView.findViewById(R.id.linear_layout_ranking_container);
        endGroupButton = fragmentView.findViewById(R.id.button_end_group_stage);
        linearProgressIndicator = fragmentView.findViewById(R.id.linear_progress_indicator_main_fixture);

        int finishedMatches = (int) fixture.GroupMatches.stream()
                .filter(x -> x.PlayerOneStats.SetsWon != null && x.PlayerTwoStats.SetsWon != null)
                .count();

        fixtureViewModel = new ViewModelProvider(this).get(FixtureViewModel.class);
        fixtureViewModel.setFixtureGroup(fixture.GroupMatches.size(), finishedMatches, fixture.State != FixtureState.GroupsStage);

        fixtureViewModel.getFixtureGroupState().observe(getViewLifecycleOwner(), fixtureGroupState -> {
            if (fixtureGroupState == null) {
                return;
            }

            if (fixtureGroupState.displayEndGroupStageButton()) {
                endGroupButton.setVisibility(View.VISIBLE);
            } else {
                endGroupButton.setVisibility(View.GONE);
            }
        });

        expandRelevantSection();
        setOnClickToExpandButtons();
        populateFixtureInformation(fixture);
        populateParticipantsList(fixture.Players);
        populateChipGroup(fixture.GroupMatches);

        if (fixture.State == FixtureState.GroupsStage) bindEndGroupStageButton();
        if (fixture.Pyramids != null && fixture.Pyramids.size() > 0) populatePyramids();
        if (fixture.Ranking != null && fixture.Ranking.size() > 0) populateFixtureRanking();
    }

    private void expandRelevantSection() {
        if (fixture.Pyramids == null || fixture.Pyramids.size() == 0) {
            pyramids_linear_fixture_content_container.setVisibility(View.GONE);
            fragmentView.findViewById(R.id.view_pyramids_delimiter).setVisibility(View.GONE);
        }

        if (fixture.Ranking == null || fixture.Ranking.size() == 0) {
            ranking_linear_fixture_content_container.setVisibility(View.GONE);
        }

        if (fixture.State == FixtureState.GroupsStage) {
            groups_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            groups_linear_layout.setVisibility(View.VISIBLE);
            groupsIsExpanded = true;
        } else if (fixture.State == FixtureState.PyramidsStage) {
            pyramids_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            pyramids_linear_layout.setVisibility(View.VISIBLE);
            pyramidsIsExpanded = true;
        } else if (fixture.State == FixtureState.Finished) {
            ranking_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            ranking_linear_layout.setVisibility(View.VISIBLE);
            rankingIsExpanded = true;
        }
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
            if (groupsIsExpanded) {
                groups_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                groups_linear_layout.setVisibility(View.GONE);
            } else {
                groups_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                groups_linear_layout.setVisibility(View.VISIBLE);
            }

            groupsIsExpanded = !groupsIsExpanded;
        });

        pyramids_expand_button.setOnClickListener(v -> {
            if (pyramidsIsExpanded) {
                pyramids_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                pyramids_linear_layout.setVisibility(View.GONE);
            } else {
                pyramids_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                pyramids_linear_layout.setVisibility(View.VISIBLE);
            }

            pyramidsIsExpanded = !pyramidsIsExpanded;
        });

        ranking_expand_button.setOnClickListener(v -> {
            if (rankingIsExpanded) {
                ranking_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                ranking_linear_layout.setVisibility(View.GONE);
            } else {
                ranking_expand_button.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                ranking_linear_layout.setVisibility(View.VISIBLE);
            }

            rankingIsExpanded = !rankingIsExpanded;
        });
    }

    private void populateFixtureInformation(@NonNull FixtureModel fixture) {
        TextView participantsTitle = fragmentView.findViewById(R.id.text_view_participants_title);
        TextView locationTextView = fragmentView.findViewById(R.id.text_view_main_fixture_location_placeholder);
        TextView dateTextView = fragmentView.findViewById(R.id.text_view_main_fixture_date_placeholder);
        TextView qualityAverageTextView = fragmentView.findViewById(R.id.text_view_main_fixture_quality_average_placeholder);
        stateTextView = fragmentView.findViewById(R.id.text_view_main_fixture_state_placeholder);

        participantsTitle.setText(String.format(Locale.getDefault(), "Participants (%d)", fixture.Players.size()));
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

            int finalI = i;
            chip.setOnClickListener(v -> {
                chipGroup.clearCheck();
                ((Chip) v).setChecked(true);

                displayGroupTable(groups.get(finalI));
            });

            if (i == 0) {
                chip.setChecked(true);
                displayGroupTable(groups.get(finalI));
            }

            chipGroup.addView(chip);
        }
    }

    private void displayGroupTable(Group group) {
        List<GroupMatch> groupMatchesForGroup = fixture.GroupMatches.stream().filter(x -> x.Group == group).collect(Collectors.toList());

        String GROUP_FRAGMENT_TAG = String.format("FRAGMENT_GROUP_%s_%s", fixture.FixtureId,
                groupMatchesForGroup.stream().findFirst().orElseGet(null).Group.name());

        GroupFragment groupFragment = (GroupFragment) fragmentManager.findFragmentByTag(GROUP_FRAGMENT_TAG);

        if (groupFragment == null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view_group,
                            GroupFragment.newInstance(gson.toJson(fixture), gson.toJson(groupMatchesForGroup)),
                            GROUP_FRAGMENT_TAG)
                    .commit();
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            for (Fragment fragment : fragmentManager.getFragments()) {
                transaction.hide(fragment);
            }

            transaction.setReorderingAllowed(true)
                    .show(groupFragment)
                    .commit();
        }
    }

    private void bindEndGroupStageButton() {
        String endGroupStageURL = ApiRoutes.END_GROUP_STAGE_ROUTE(fixture.SeasonId.toString(), fixture.FixtureId.toString());

        endGroupButton.setOnClickListener(v -> {
            linearProgressIndicator.show();
            v.setEnabled(false);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endGroupStageURL, null,
                    this::refreshFixtureFragment,
                    error -> {
                        linearProgressIndicator.hide();
                        linearProgressIndicator.setVisibility(View.GONE);
                        v.setEnabled(true);
                    }
            );

            requestQueue.add(increaseTimeout(jsonObjectRequest));
        });
    }

    private void populatePyramids() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_fixture_pyramids);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fixture.Pyramids.sort(Comparator.comparing(Pyramid::getType));

        RecyclerView.Adapter<RecyclerView.ViewHolder> fixturePyramids = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return PyramidItemViewHolder.create(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                bind((PyramidItemViewHolder) viewHolder, position);
            }

            @Override
            public int getItemCount() {
                return fixture.Pyramids.size();
            }

            private void bind(@NonNull PyramidItemViewHolder vh, int position) {
                Pyramid pyramid = fixture.Pyramids.get(position);

                String[] deciderRanks = pyramid.Type.toString().split("_");

                vh.pyramidTitle.setText(String.format("Deciders %s - %s", deciderRanks[1], deciderRanks[2]));
                populatePyramidsMatches(vh.pyramidMatches, pyramid.Matches);
            }
        };

        recyclerView.setAdapter(fixturePyramids);
    }

    private void populatePyramidsMatches(@NonNull RecyclerView recyclerView, List<MatchModel> matches) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.Adapter<RecyclerView.ViewHolder> pyramidMatches = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return PyramidMatchesItemViewHolder.create(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                bind((PyramidMatchesItemViewHolder) viewHolder, position);

                viewHolder.itemView.setOnLongClickListener(v -> {
                    createAlertDialog(matches.get(position));
                    return false;
                });
            }

            @Override
            public int getItemCount() {
                return matches.size();
            }

            private void bind(@NonNull PyramidMatchesItemViewHolder vh, int position) {
                MatchModel match = matches.get(position);

                String displayOne = match.PlayerOneStats.SetsWon == null ? "" : match.PlayerOneStats.SetsWon.toString();
                String displayTwo = match.PlayerTwoStats.SetsWon == null ? "" : match.PlayerTwoStats.SetsWon.toString();

                vh.playerOneName.setText(match.PlayerOneStats.PlayerName);
                vh.matchScore.setText(String.format("%s - %s", displayOne , displayTwo));
                vh.playerTwoName.setText(match.PlayerTwoStats.PlayerName);
            }
        };

        recyclerView.setAdapter(pyramidMatches);
    }

    private void populateFixtureRanking() {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_fixture_ranking);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fixture.Ranking.sort(Comparator.comparing(PlayerRank::getRank));

        RecyclerView.Adapter<RecyclerView.ViewHolder> fixtureRanking = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return FixtureRankingItemViewHolder.create(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                bind((FixtureRankingItemViewHolder) viewHolder, position);
            }

            @Override
            public int getItemCount() {
                return fixture.Ranking.size();
            }

            private void bind(@NonNull FixtureRankingItemViewHolder vh, int position) {
                PlayerRank playerRank = fixture.Ranking.get(position);

                String extraWhiteSpace = playerRank.Rank < 10 ? "   " : " ";
                vh.playerRank.setText(String.format(Locale.getDefault(), "%d.%s", playerRank.Rank, extraWhiteSpace));
                vh.playerName.setText(playerRank.PlayerName);
                vh.playerScore.setText(String.format(Locale.getDefault(), "%.2f", playerRank.Score));
            }
        };

        recyclerView.setAdapter(fixtureRanking);
    }

    private void createAlertDialog(@NonNull MatchModel match) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getView().getContext());

        final View alertDialogView = getLayoutInflater().inflate(R.layout.alert_dialog_edit_match_score, null);

        builder.setTitle("Set score");
        builder.setView(alertDialogView);
        AlertDialog alertDialog = builder.create();

        alertDialog.show();

        TextView playerOneNameTextView = alertDialogView.findViewById(R.id.text_view_match_score_player_one_name);
        TextView playerTwoNameTextView = alertDialogView.findViewById(R.id.text_view_match_score_player_two_name);
        EditText playerOneScoreEditText = alertDialogView.findViewById(R.id.edit_text_match_score_player_one_score);
        EditText playerTwoScoreEditText = alertDialogView.findViewById(R.id.edit_text_match_score_player_two_score);
        Button cancelButton = alertDialogView.findViewById(R.id.button_set_score_dismiss);
        Button saveButton = alertDialogView.findViewById(R.id.button_set_score_save);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(!Util.isNullOrEmpty(playerOneScoreEditText.getText().toString()) &&
                        !Util.isNullOrEmpty(playerTwoScoreEditText.getText().toString()));
            }
        };

        playerOneScoreEditText.addTextChangedListener(afterTextChangedListener);
        playerTwoScoreEditText.addTextChangedListener(afterTextChangedListener);

        playerOneNameTextView.setText(match.PlayerOneStats.PlayerName);
        playerTwoNameTextView.setText(match.PlayerTwoStats.PlayerName);

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());

        saveButton.setOnClickListener(v -> {
            linearProgressIndicator.show();

            String playerOneScore = playerOneScoreEditText.getText().toString();
            String playerTwoScore = playerTwoScoreEditText.getText().toString();

            int p1Score = Integer.parseInt(playerOneScore);
            int p2Score = Integer.parseInt(playerTwoScore);

            if (p1Score == p2Score) {
                playerTwoScoreEditText.setError("Scores must be different");
            } else {
                try {
                    updateMatch(match.MatchId, p1Score, p2Score);
                } catch (JSONException e) {
                    Log.e("FixtureFragment",
                            String.format("Updating decider match %s failed.", match.MatchId), e);
                }

                alertDialog.dismiss();
            }
        });
    }

    private void updateMatch(@NonNull UUID matchId, int p1Score, int p2Score) throws JSONException {
        final String patchGroupMatchURL =
                ApiRoutes.PATCH_FIXTURE_DECIDER_MATCH_ROUTE(fixture.SeasonId.toString(), fixture.FixtureId.toString(), matchId.toString());

        MatchPutDTO matchPutDTO = new MatchPutDTO(p1Score, p2Score);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH,
                patchGroupMatchURL, new JSONObject(gson.toJson(matchPutDTO)),
                this::refreshFixtureFragment,
                error -> {
                    linearProgressIndicator.hide();
                    linearProgressIndicator.setVisibility(View.GONE);
                    Log.e("FixtureFragment", error != null & error.getMessage() != null ?
                            error.getMessage() : "PATCH match crashed.");
                }
        );

        requestQueue.add(increaseTimeout(jsonObjectRequest));
    }

    private void refreshFixtureFragment(@NonNull JSONObject response) {
        fixture = gson.fromJson(response.toString(), FixtureModel.class);

        String FIXTURE_FRAGMENT_TAG = String.format("FRAGMENT_FIXTURE_%s", fixture.FixtureId);

        FixtureFragment thisFragment = (FixtureFragment) getParentFragmentManager().findFragmentByTag(FIXTURE_FRAGMENT_TAG);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        transaction.remove(thisFragment);
        transaction.add(R.id.fragment_container_view_season_content,
                FixtureFragment.newInstance(gson.toJson(fixture)),
                FIXTURE_FRAGMENT_TAG);

        transaction.setReorderingAllowed(true).commit();
    }

}
