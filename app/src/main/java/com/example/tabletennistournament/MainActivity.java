package com.example.tabletennistournament;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tabletennistournament.models.SeasonModel;
import com.example.tabletennistournament.models.SeasonPlayerModel;
import com.example.tabletennistournament.modules.players.PlayersActivity;
import com.example.tabletennistournament.modules.upcoming.NextFixturesActivity;
import com.example.tabletennistournament.services.ApiRoutes;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Gson gson;
    RequestQueue requestQueue;
    CircularProgressIndicator progressIndicator;
    MaterialToolbar topBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();
        requestQueue = Volley.newRequestQueue(this);
        progressIndicator = findViewById(R.id.circular_progress_indicator_main);
        topBar = findViewById(R.id.topAppBar_main);

        getSeasons();
    }

    public void navigateToUpcomingFixturesActivity(@NonNull MenuItem item) {
        Intent intent = new Intent(this, NextFixturesActivity.class);
        startActivity(intent);
    }

    public void navigateToPlayersActivity(@NonNull MenuItem item) {
        Intent intent = new Intent(this, PlayersActivity.class);
        startActivity(intent);
    }

    private void getSeasons() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ApiRoutes.SEASONS_ROUTE, null,
                response -> {
                    List<SeasonModel> seasonModels = gson.fromJson(response.toString(), new TypeToken<List<SeasonModel>>() {
                    }.getType());
                    seasonModels.sort(Comparator.comparing(SeasonModel::getNumber).reversed());

                    getSeasonPlayers(seasonModels.get(0));
                    setMenu(seasonModels);
                },
                error -> handleError()
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void setMenu(@NonNull List<SeasonModel> seasonModels) {
        Menu menu = topBar.getMenu();

        for (SeasonModel seasonModel : seasonModels) {
            menu.add(Menu.NONE, seasonModel.getNumber(), Menu.NONE, "Season " + seasonModel.getNumber());
            MenuItem menuItem = menu.findItem(seasonModel.getNumber());
            menuItem.setOnMenuItemClickListener(item -> {
                getSeasonPlayers(seasonModel);
                return true;
            });
        }
    }

    private void getSeasonPlayers(@NonNull SeasonModel seasonModel) {
        topBar.setTitle("Season " + seasonModel.Number);

        String seasonPlayersUrl = String.format("%s/%s/players", ApiRoutes.SEASONS_ROUTE, seasonModel.SeasonId);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, seasonPlayersUrl, null,
                response -> {
                    List<SeasonPlayerModel> players = gson.fromJson(response.toString(), new TypeToken<List<SeasonPlayerModel>>() {
                    }.getType());
                    players.sort(Comparator.comparing(SeasonPlayerModel::getRank));

                    progressIndicator.hide();
                },
                error -> handleError()
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void handleError() {
        TextView serverErrorTextView = findViewById(R.id.text_view_server_error_main);
        serverErrorTextView.setText(R.string.server_error);
        progressIndicator.hide();
    }

}