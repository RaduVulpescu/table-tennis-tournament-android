package com.example.tabletennistournament;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.tabletennistournament.models.SeasonPlayerModel;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Comparator;
import java.util.List;

public class RankingFragment extends Fragment {

    Gson gson;
    RequestQueueSingleton requestQueue;
    CircularProgressIndicator progressIndicator;
    TextView serverErrorTextView;
    Button reloadButton;

    String seasonId;

    public static final String BUNDLE_SEASON_ID = "BUNDLE_SEASON_ID";

    public RankingFragment() {
        super(R.layout.ranking_fragment);
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
        seasonId = argumentsBundle.getString(BUNDLE_SEASON_ID);

        getSeasonPlayers();
    }

    public void getSeasonPlayers() {
        String seasonPlayersUrl = String.format("%s/%s/players", ApiRoutes.SEASONS_ROUTE, seasonId);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, seasonPlayersUrl, null,
                response -> {
                    List<SeasonPlayerModel> players = gson.fromJson(response.toString(), new TypeToken<List<SeasonPlayerModel>>() {
                    }.getType());
                    players.sort(Comparator.comparing(SeasonPlayerModel::getRank));

                    progressIndicator.hide();
                },
                error -> {
                    progressIndicator.hide();
                    reloadButton.setVisibility(View.VISIBLE);
                    serverErrorTextView.setVisibility(View.VISIBLE);
                }
        );

        requestQueue.add(jsonArrayRequest);
    }
}
