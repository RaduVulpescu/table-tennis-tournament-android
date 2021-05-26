package com.example.tabletennistournament;

import android.os.Bundle;
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
import com.example.tabletennistournament.models.SeasonPlayerModel;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.example.tabletennistournament.services.Common.getPlayerLevelIcon;
import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class RankingFragment extends Fragment {

    Gson gson;
    RequestQueueSingleton requestQueue;
    CircularProgressIndicator progressIndicator;
    TextView serverErrorTextView;
    Button reloadButton;
    View view;

    String seasonId;

    public static final String BUNDLE_SEASON_ID = "BUNDLE_SEASON_ID";

    public RankingFragment() {
        super(R.layout.fragment_ranking);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle argumentsBundle = getArguments();
        if (argumentsBundle == null) return;

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(getActivity().getBaseContext());
        progressIndicator = view.findViewById(R.id.circular_progress_indicator_ranking);
        serverErrorTextView = view.findViewById(R.id.text_view_server_error_ranking);
        reloadButton = view.findViewById(R.id.button_reload_ranking);
        reloadButton.setOnClickListener(v -> getPlayers());
        seasonId = argumentsBundle.getString(BUNDLE_SEASON_ID);
        this.view = view;

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
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_ranking);
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

                vh.playerScore1.setText(String.format("Score 1: %s", player.Score1));
                vh.playerScore2.setText(String.format("Score 2: %s", player.Score2));
                vh.playerScore3.setText(String.format("Score 3: %s", player.Score3));
                vh.playerScore4.setText(String.format("Score 4: %s", player.Score4));

                DecimalFormat decimalFormat = new DecimalFormat("+#,##0.0000;-#");
                vh.playerShape.setText(String.format("Shape:   %s", decimalFormat.format(player.Shape)));

                vh.expandButton.setOnClickListener(v -> {
                    v.setVisibility(View.GONE);
                    vh.linearLayoutExtraStats.setVisibility(View.VISIBLE);
                    vh.collapseButton.setVisibility(View.VISIBLE);
                });

                vh.collapseButton.setOnClickListener(v -> {
                    v.setVisibility(View.GONE);
                    vh.linearLayoutExtraStats.setVisibility(View.GONE);
                    vh.expandButton.setVisibility(View.VISIBLE);
                });
            }
        };

        recyclerView.setAdapter(rankingAdapter);
    }
}
