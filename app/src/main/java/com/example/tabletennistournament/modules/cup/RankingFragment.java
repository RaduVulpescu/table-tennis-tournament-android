package com.example.tabletennistournament.modules.cup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.SeasonPlayerModel;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.GsonSingleton;
import com.example.tabletennistournament.services.LoginRepository;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.tabletennistournament.services.Common.getPlayerLevelIcon;
import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class RankingFragment extends Fragment {

    private static final String ARG_SEASON_ID = "ARG_SEASON_ID";
    private static final String ARG_END_DATE_JSON = "ARG_END_DATE_JSON";

    String seasonId;
    String endDateJson;
    Date endDate;

    Gson gson;
    RequestQueueSingleton requestQueue;

    View fragmentView;
    CircularProgressIndicator progressIndicator;
    TextView serverErrorTextView;
    Button reloadButton;
    LinearProgressIndicator linearProgressIndicator;
    ExtendedFloatingActionButton endSeasonButton;
    TextView noFixtureFinished;

    public RankingFragment() {
    }

    @NonNull
    public static RankingFragment newInstance(String seasonId, String endDateJson) {
        RankingFragment fragment = new RankingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEASON_ID, seasonId);
        args.putString(ARG_END_DATE_JSON, endDateJson);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null) return;

        seasonId = getArguments().getString(ARG_SEASON_ID);
        endDateJson = getArguments().getString(ARG_END_DATE_JSON);

        gson = GsonSingleton.getInstance();
        requestQueue = RequestQueueSingleton.getInstance(getActivity().getBaseContext());

        endDate = endDateJson.equals("null") ? null : gson.fromJson(endDateJson, Date.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_ranking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentView = view;

        progressIndicator = view.findViewById(R.id.circular_progress_indicator_ranking);
        serverErrorTextView = view.findViewById(R.id.text_view_server_error_ranking);
        reloadButton = view.findViewById(R.id.button_reload_ranking);
        reloadButton.setOnClickListener(v -> getPlayers());
        endSeasonButton = view.findViewById(R.id.floating_action_button_end_season);
        linearProgressIndicator = view.findViewById(R.id.linear_progress_indicator_main_ranking);
        noFixtureFinished = view.findViewById(R.id.text_view_no_fixture_finished);

        getPlayers();
    }

    public void getPlayers() {
        progressIndicator.show();
        reloadButton.setVisibility(View.GONE);
        serverErrorTextView.setVisibility(View.GONE);

        String seasonPlayersUrl = String.format("%s/%s/players", ApiRoutes.SEASONS_ROUTE, seasonId);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, seasonPlayersUrl, null,
                response -> {
                    List<SeasonPlayerModel> players = gson.fromJson(response.toString(), new TypeToken<List<SeasonPlayerModel>>() {
                    }.getType());
                    players.sort(Comparator.comparing(SeasonPlayerModel::getRank));

                    progressIndicator.hide();
                    if (players.size() == 0) noFixtureFinished.setVisibility(View.VISIBLE);

                    if (endDate == null && players.size() > 0) bindEndSeasonButton();
                    createRankingRecyclerView(players);
                },
                error -> {
                    progressIndicator.hide();
                    reloadButton.setVisibility(View.VISIBLE);
                    serverErrorTextView.setVisibility(View.VISIBLE);
                }
        );

        requestQueue.add(increaseTimeout(jsonArrayRequest));
    }

    private void createRankingRecyclerView(List<SeasonPlayerModel> players) {
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view_ranking);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.Adapter<RecyclerView.ViewHolder> rankingAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return RankingItemViewHolder.create(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                bind((RankingItemViewHolder) viewHolder, position);
            }

            @Override
            public int getItemCount() {
                return players.size();
            }

            private void bind(@NonNull RankingItemViewHolder vh, int position) {
                SeasonPlayerModel player = players.get(position);

                vh.playerRank.setText(String.valueOf(player.Rank));
                vh.playerName.setText(player.Name);
                vh.playerLevelIcon.setImageResource(getPlayerLevelIcon(player.Level));
                vh.playerQuality.setText(String.format(Locale.getDefault(), "Q: %.2f", player.Quality));
                vh.playerTop4.setText(String.format(Locale.getDefault(), "Top 4: %.2f", player.Top4));

                vh.playerScore1.setText(String.format(Locale.getDefault(), "Score 1: %.2f", player.Score1));
                vh.playerScore2.setText(String.format(Locale.getDefault(), "Score 2: %.2f", player.Score2));
                vh.playerScore3.setText(String.format(Locale.getDefault(), "Score 3: %.2f", player.Score3));
                vh.playerScore4.setText(String.format(Locale.getDefault(), "Score 4: %.2f", player.Score4));

                DecimalFormat decimalFormat = new DecimalFormat("+#,##0.0000;-#");
                vh.playerShape.setText(String.format("Shape:   %s", decimalFormat.format(player.Shape)));

                vh.expandButton.setOnClickListener(v -> {
                    TransitionManager.beginDelayedTransition(vh.cardView, new AutoTransition());

                    if (vh.isExpanded) {
                        vh.linearLayoutExtraStats.setVisibility(View.GONE);
                        vh.expandButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    } else {
                        vh.linearLayoutExtraStats.setVisibility(View.VISIBLE);
                        vh.expandButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    }

                    vh.isExpanded = !vh.isExpanded;
                });
            }
        };

        recyclerView.setAdapter(rankingAdapter);
    }

    private void bindEndSeasonButton() {
        if (!LoginRepository.getInstance().isLoggedIn()) {
            return;
        }

        endSeasonButton.setVisibility(View.VISIBLE);
        final String endSeasonURL = ApiRoutes.END_SEASON_ROUTE(seasonId);

        endSeasonButton.setOnClickListener(v -> {
            linearProgressIndicator.show();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endSeasonURL, null,
                    response -> getActivity().recreate(),
                    error -> {
                        linearProgressIndicator.hide();
                        linearProgressIndicator.setVisibility(View.GONE);
                        endSeasonButton.setVisibility(View.VISIBLE);

                        Snackbar snackbar = Snackbar.make(getView(), R.string.server_error, Snackbar.LENGTH_LONG);
                        snackbar.setAnchorView(fragmentView.findViewById(R.id.floating_action_button_end_season));
                        snackbar.show();
                    }
            );

            requestQueue.add(increaseTimeout(jsonObjectRequest));
        });
    }

}
