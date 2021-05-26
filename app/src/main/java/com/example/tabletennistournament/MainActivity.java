package com.example.tabletennistournament;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.tabletennistournament.models.SeasonModel;
import com.example.tabletennistournament.modules.players.PlayersActivity;
import com.example.tabletennistournament.modules.upcoming.NextFixturesActivity;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Comparator;
import java.util.List;

import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class MainActivity extends AppCompatActivity {

    Gson gson;
    RequestQueueSingleton requestQueue;
    CircularProgressIndicator progressIndicator;
    MaterialToolbar topBar;
    TextView serverErrorTextView;
    Button reloadButton;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(this);
        progressIndicator = findViewById(R.id.circular_progress_indicator_main);
        topBar = findViewById(R.id.topAppBar_main);
        serverErrorTextView = findViewById(R.id.text_view_server_error_main);
        reloadButton = findViewById(R.id.button_reload_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation_ranking);

        setBottomNavigationBar();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view_cup, RankingFragment.class, null)
                    .commit();
        }

        getSeasons(null);
    }

    public void getSeasons(View view) {
        reloadButton.setVisibility(View.GONE);
        serverErrorTextView.setVisibility(View.GONE);
        progressIndicator.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ApiRoutes.SEASONS_ROUTE, null,
                response -> {
                    List<SeasonModel> seasonModels = gson.fromJson(response.toString(), new TypeToken<List<SeasonModel>>() {
                    }.getType());
                    seasonModels.sort(Comparator.comparing(SeasonModel::getNumber).reversed());

                    SeasonModel currentSeason = seasonModels.get(0);

                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.current_season_id), currentSeason.SeasonId.toString());
                    editor.apply();

                    getSeasonPlayers(currentSeason);
                    setMenu(seasonModels);
                    progressIndicator.hide();
                },
                error -> {
                    reloadButton.setVisibility(View.VISIBLE);
                    serverErrorTextView.setVisibility(View.VISIBLE);
                    progressIndicator.hide();
                }
        );

        requestQueue.add(increaseTimeout(jsonArrayRequest));
    }

    @SuppressLint("NonConstantResourceId")
    private void setBottomNavigationBar() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_button_ranking);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_button_upcoming: {
                    Intent intent = new Intent(this, NextFixturesActivity.class);
                    startActivity(intent);
                    return true;
                }
                case R.id.navigation_button_ranking: {
                    return true;
                }
                case R.id.navigation_button_players: {
                    Intent intent = new Intent(this, PlayersActivity.class);
                    startActivity(intent);
                    return true;
                }
                default:{
                    return false;
                }
            }
        });
    }

    private void setMenu(@NonNull List<SeasonModel> seasonModels) {
        Menu menu = topBar.getMenu();

        if (menu.size() == 1) {
            for (SeasonModel seasonModel : seasonModels) {
                menu.add(Menu.NONE, seasonModel.getNumber(), Menu.NONE, "Season " + seasonModel.getNumber());
                MenuItem menuItem = menu.findItem(seasonModel.getNumber());
                menuItem.setOnMenuItemClickListener(item -> {
                    getSeasonPlayers(seasonModel);
                    return true;
                });
            }
        }
    }

    private void getSeasonPlayers(@NonNull SeasonModel seasonModel) {
        topBar.setTitle("Season " + seasonModel.Number);

        Bundle bundle = new Bundle();
        bundle.putString(RankingFragment.BUNDLE_SEASON_ID, seasonModel.SeasonId.toString());

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view_cup, RankingFragment.class, bundle)
                .commit();
    }
}